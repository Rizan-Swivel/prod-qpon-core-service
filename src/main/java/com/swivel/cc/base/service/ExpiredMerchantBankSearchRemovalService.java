package com.swivel.cc.base.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class ExpiredMerchantBankSearchRemovalService {

    private static final String CRON = "0 0 0 * * *";
    private static final String TIME_ZONE = "GMT +05:30";
    private static final int PAGE = 0;
    private static final int SIZE = 250;
    private final MerchantBankSearchIndexService merchantBankSearchIndexService;
    private final DealService dealService;

    public ExpiredMerchantBankSearchRemovalService(MerchantBankSearchIndexService merchantBankSearchIndexService,
                                                   DealService dealService) {
        this.merchantBankSearchIndexService = merchantBankSearchIndexService;
        this.dealService = dealService;
    }

    /**
     * This method execute at 00:00 am to remove expired bank deals from the bankDealSearchIndex.
     * 24/7 -> cron = "0 0 0 * * *"
     */
    @Scheduled(cron = CRON, zone = TIME_ZONE)
    private void removeExpiredSearchBankDeal() {
        Pageable pageable = PageRequest.of(PAGE, SIZE);
        Page<String> allBankDealIdPage = merchantBankSearchIndexService.getAllBankDealIdList(pageable);
        List<String> allBankDealIdList = allBankDealIdPage.toList();
        for (int page = 1; page < allBankDealIdPage.getTotalPages(); page++) {
            pageable = PageRequest.of(page, SIZE);
            allBankDealIdList.addAll(merchantBankSearchIndexService.getAllBankDealIdList(pageable).toList());
        }
        var expiredDealIds = getExpiredDealIds(allBankDealIdList);
        merchantBankSearchIndexService.removeExpiredDeals(expiredDealIds);
        merchantBankSearchIndexService.removeInactiveBanks();
        merchantBankSearchIndexService.removeInactiveMerchants();
    }

    /**
     * This method returns the expired bank deal list.
     *
     * @param allBankDealIds bank dealIds list
     * @return expired bank deal list
     */
    private List<String> getExpiredDealIds(List<String> allBankDealIds) {
        List<String> expiredBankDealIds = new ArrayList<>();
        allBankDealIds.forEach(id -> {
            boolean isDealExpired = dealService.checkDealIsExpired(id);
            if (isDealExpired) {
                expiredBankDealIds.add(id);
            }
        });
        return expiredBankDealIds;
    }
}