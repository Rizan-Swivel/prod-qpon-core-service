package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.CategoryBrandMerchantIndex;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.UserType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MerchantUpdateService {

    private static Map<String, MerchantBusinessResponseDto> merchantMapFromAuth = new HashMap<>();
    private final int SIZE;
    private final String USER = "user-id";
    private final String CRON = "0 0 0 * * *";
    private final String TIME_ZONE = "GMT +05:30";
    private final AuthUserService authUserService;
    private final DealSearchService dealSearchService;
    private final List<String> merchantIdListFromDb = new ArrayList<>();
    private final List<String> infoChangedMerchantIds = new ArrayList<>();
    private final CategoryBrandMerchantService categoryBrandMerchantService;
    private final RequestADealSearchIndexService requestADealSearchIndexService;
    private final DealsOfTheDaySearchIndexService dealsOfTheDaySearchIndexService;
    private final CategoryBrandMerchantIndexService categoryBrandMerchantIndexService;
    private final Map<String, MerchantBusinessResponseDto> changedMerchantsMap = new HashMap<>();
    private final Map<String, BasicMerchantBusinessResponseDto> merchantIndexMapFromDb = new HashMap<>();

    @Autowired
    public MerchantUpdateService(
            @Value("${dataUpdateScheduler.size}") int size,
            AuthUserService authUserService,
            DealSearchService dealSearchService,
            CategoryBrandMerchantService categoryBrandMerchantService,
            RequestADealSearchIndexService requestADealSearchIndexService,
            DealsOfTheDaySearchIndexService dealsOfTheDaySearchIndexService,
            CategoryBrandMerchantIndexService categoryBrandMerchantIndexService) {
        this.authUserService = authUserService;
        this.dealSearchService = dealSearchService;
        this.categoryBrandMerchantService = categoryBrandMerchantService;
        this.requestADealSearchIndexService = requestADealSearchIndexService;
        this.dealsOfTheDaySearchIndexService = dealsOfTheDaySearchIndexService;
        this.categoryBrandMerchantIndexService = categoryBrandMerchantIndexService;
        this.SIZE = size;
    }

    /**
     * This method execute every day 00:00 am to update merchant data.
     */
    @Scheduled(cron = CRON, zone = TIME_ZONE)
    private void getApprovedMerchantsFromCategoryBrandMerchantIndex() {
        int totalPageCount;
        int page = 0;
        Pageable pageable = PageRequest.of(page, SIZE);
        var approvedMerchantInfo =
                categoryBrandMerchantIndexService.getAllMerchantInfo(pageable);
        totalPageCount = approvedMerchantInfo.getTotalPages();
        if (approvedMerchantInfo.getTotalPages() > 1) {
            addToMap(approvedMerchantInfo);
            while (page < totalPageCount) {
                Pageable incrementalPageable = PageRequest.of(page + 1, SIZE);
                var content =
                        categoryBrandMerchantIndexService.getAllMerchantInfo(incrementalPageable);
                addToMap(content);
                page++;
            }
        } else {
            addToMap(approvedMerchantInfo);
        }
        getBulkMerchantInfoFromAuth(merchantIdListFromDb);
    }

    /**
     * Check Difference between auth service merchant data and core service
     *
     * @param merchantMapFromAuth Merchant id & MerchantBusinessResponseDto map
     * @param indexMerchantMap    Merchant id & BasicMerchantBusinessResponseDto map
     */
    public void checkForMerchantDetailsChanges(Map<String, MerchantBusinessResponseDto> merchantMapFromAuth,
                                               Map<String, BasicMerchantBusinessResponseDto> indexMerchantMap) {
        for (var merchant : merchantMapFromAuth.entrySet()) {
            var merchantFromAuth = merchant.getValue();
            var merchantFromDb = indexMerchantMap.get(merchant.getKey());
            if (merchantFromAuth.getId().equals(merchantFromDb.getId())) {
                if (!merchantFromAuth.getName().equals(merchantFromDb.getName())
                        || !merchantFromAuth.getImageUrl().equals(merchantFromDb.getImageUrl())
                        || !merchantFromAuth.getApprovalStatus().equals(merchantFromDb.getApprovalStatus())
                        || merchantFromAuth.isActive() != merchantFromDb.isActive()) {
                    changedMerchantsMap.put(merchant.getKey(), merchant.getValue());
                    infoChangedMerchantIds.add(merchant.getKey());
                }
            }
        }
        if (infoChangedMerchantIds.size() > 0) {
            updateMerchantInfo();
            clearAllMaps();
        } else {
            clearAllMaps();
            return;
        }
    }

    /**
     * This method clear all fields.
     */
    private void clearAllMaps() {
        changedMerchantsMap.clear();
        merchantIndexMapFromDb.clear();
        merchantMapFromAuth.clear();
        merchantIdListFromDb.clear();
        infoChangedMerchantIds.clear();
    }

    /**
     * This method will update merchant data.
     */
    private void updateMerchantInfo() {
        categoryBrandMerchantIndexService.updateMerchantInfo(infoChangedMerchantIds, changedMerchantsMap);
        categoryBrandMerchantService.updateMerchantInfo(infoChangedMerchantIds, changedMerchantsMap);
        dealSearchService.updateMerchantInfo(infoChangedMerchantIds, changedMerchantsMap);
        dealsOfTheDaySearchIndexService.updateMerchantInfo(infoChangedMerchantIds, changedMerchantsMap);
        requestADealSearchIndexService.updateMerchantInfo(infoChangedMerchantIds, changedMerchantsMap);
    }

    /**
     * Get Merchant info from auth service
     *
     * @param merchantIdListFromDb String List
     */
    public void getBulkMerchantInfoFromAuth(List<String> merchantIdListFromDb) {
        if (merchantIdListFromDb.size() > 0) {
            BulkUserRequestDto bulkUserRequestDto = new BulkUserRequestDto(merchantIdListFromDb);
            merchantMapFromAuth = authUserService.getMerchantMap(USER, bulkUserRequestDto, UserType.MERCHANT);
            checkForMerchantDetailsChanges(merchantMapFromAuth, merchantIndexMapFromDb);
        }
    }

    /**
     * Add CategoryBrandMerchantIndexMerchant In to Map
     *
     * @param approvedMerchantInfo CategoryBrandMerchant Page
     */
    private void addToMap(Page<CategoryBrandMerchantIndex> approvedMerchantInfo) {
        for (var categoryBrandMerchant : approvedMerchantInfo.getContent()) {
            var basicMerchant = new BasicMerchantBusinessResponseDto(categoryBrandMerchant);
            merchantIndexMapFromDb.put(categoryBrandMerchant.getMerchantId(), basicMerchant);
            merchantIdListFromDb.add(categoryBrandMerchant.getMerchantId());
        }
    }
}