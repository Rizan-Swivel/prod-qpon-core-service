package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.BankMerchantSearchIndex;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.BankMerchantSearchIndexRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * BankMerchantSearchIndex service
 */
@Service
@Slf4j
public class MerchantBankSearchIndexService {

    private static final String ALL = "ALL";
    private static final int PAGE = 0;
    private static final int SIZE = 250;
    private static final String READING_BANK_DEAL_SEARCH_INDEX_FAILED = "Reading BankMerchantSearchIndex was failed";
    private final BankMerchantSearchIndexRepository bankMerchantSearchIndexRepository;
    private final AuthUserService authUserService;


    @Autowired
    public MerchantBankSearchIndexService(BankMerchantSearchIndexRepository bankMerchantSearchIndexRepository,
                                          AuthUserService authUserService) {
        this.bankMerchantSearchIndexRepository = bankMerchantSearchIndexRepository;
        this.authUserService = authUserService;
    }

    /**
     * This method save a bankDealSearchIndex.
     *
     * @param bankDeal         bank deal
     * @param bankBusiness     basicMerchantBusinessResponseDto
     * @param merchantBusiness basicMerchantBusinessResponseDto
     */
    public void save(BankDeal bankDeal,
                     BasicMerchantBusinessResponseDto bankBusiness,
                     BusinessMerchantResponseDto merchantBusiness) {
        try {
            var bankDealSearchIndex = new BankMerchantSearchIndex(bankDeal.getId(), bankBusiness, merchantBusiness);
            bankMerchantSearchIndexRepository.save(bankDealSearchIndex);
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving bankDeal search index to database was failed", e);
        }
    }

    /**
     * This method returns BankMerchantSearchIndex page by bank id and search term.
     *
     * @param pageable   pageable
     * @param bankId     bank id
     * @param searchTerm search term
     * @return BankMerchantSearchIndex page
     */
    public Page<BankMerchantSearchIndex> getBankDealSearchIndexByBankIdAndSearchTerm(Pageable pageable,
                                                                                     String bankId,
                                                                                     String searchTerm,
                                                                                     boolean onlyActiveMerchant) {
        try {
            if (onlyActiveMerchant) {
                if (ALL.equals(searchTerm)) {
                    return bankMerchantSearchIndexRepository.findAllByBankIdAndIsActiveMerchantTrue(pageable, bankId);
                } else {
                    return bankMerchantSearchIndexRepository
                            .findAllByBankIdAndIsActiveMerchantTrueAndMerchantNameContaining(pageable, bankId, searchTerm);
                }
            } else {
                if (ALL.equals(searchTerm)) {
                    return bankMerchantSearchIndexRepository.findAllByBankId(pageable, bankId);
                } else {
                    return bankMerchantSearchIndexRepository
                            .findAllByBankIdAndMerchantNameContaining(pageable, bankId, searchTerm);
                }
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(READING_BANK_DEAL_SEARCH_INDEX_FAILED, e);
        }
    }

    /**
     * This method returns BankMerchantSearchIndex page by merchant id, Search term and isActiveBank
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return BankMerchantSearchIndex page
     */
    public Page<BankMerchantSearchIndex> getBankMerchantSearchIndexByMerchantIdAndSearchTerm(
            Pageable pageable, String merchantId, String searchTerm, boolean onlyActiveBank) {
        try {
            if (onlyActiveBank) {
                if (ALL.equals(searchTerm)) {
                    return bankMerchantSearchIndexRepository.findAllByMerchantIdAndIsActiveBankTrue(pageable, merchantId);
                } else {
                    return bankMerchantSearchIndexRepository
                            .findAllByMerchantIdAndBankNameContainingAndIsActiveBankTrue(pageable, merchantId, searchTerm);
                }
            } else {
                if (ALL.equals(searchTerm)) {
                    return bankMerchantSearchIndexRepository.findAllByMerchantId(pageable, merchantId);
                } else {
                    return bankMerchantSearchIndexRepository
                            .findAllByMerchantIdAndBankNameContaining(pageable, merchantId, searchTerm);
                }
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(READING_BANK_DEAL_SEARCH_INDEX_FAILED, e);
        }
    }

    /**
     * This method returns list of bankMerchant Deal ids currently have in the bankDealSearchIndex.
     *
     * @return List of All Bank Ids
     */
    public Page<String> getAllBankDealIdList(Pageable pageable) {
        try {
            return bankMerchantSearchIndexRepository.getAllDealIds(pageable);
        } catch (DataAccessException e) {
            throw new QponCoreException(READING_BANK_DEAL_SEARCH_INDEX_FAILED, e);
        }
    }

    /**
     * This method removed the given deal id list.
     *
     * @param expiredDealIds dealIds
     */
    @Transactional
    public void removeExpiredDeals(List<String> expiredDealIds) {
        try {
            expiredDealIds.forEach(bankMerchantSearchIndexRepository::deleteByFromLatestDealId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Deleting BankDealSearchIndexes from the database was failed", e);
        }
    }

    /**
     * This method removes inactive banks.
     */
    @Transactional
    public void removeInactiveBanks() {
        try {
            List<String> bankIds = bankMerchantSearchIndexRepository.getAllDistinctBankIds();
            var bulkBank = authUserService
                    .getMerchantMap(UserType.ADMIN.name(), new BulkUserRequestDto(bankIds), UserType.BANK);
            List<String> notActiveBankIds = getInactiveBankOrMerchantIds(bulkBank);
            bankMerchantSearchIndexRepository.deleteByBankIdIn(notActiveBankIds);
        } catch (DataAccessException e) {
            log.error("Removing inactive banks from db failed.");
        }
    }

    /**
     * This method removes inactive merchants.
     */
    @Transactional
    public void removeInactiveMerchants() {
        try {
            Pageable pageable = PageRequest.of(PAGE, SIZE);
            Page<String> merchantIdsPage = bankMerchantSearchIndexRepository.getAllDistinctMerchantIds(pageable);
            List<String> merchantIds = merchantIdsPage.toList();
            var bulkMerchant = authUserService
                    .getMerchantMap(UserType.ADMIN.name(), new BulkUserRequestDto(merchantIds), UserType.MERCHANT);
            List<String> notActiveMerchantIds = getInactiveBankOrMerchantIds(bulkMerchant);

            for (int page = 1; page < merchantIdsPage.getTotalPages(); page++) {
                pageable = PageRequest.of(page, SIZE);
                merchantIds = bankMerchantSearchIndexRepository.getAllDistinctMerchantIds(pageable).toList();
                bulkMerchant = authUserService
                        .getMerchantMap(UserType.ADMIN.name(), new BulkUserRequestDto(merchantIds), UserType.MERCHANT);
                notActiveMerchantIds.addAll(getInactiveBankOrMerchantIds(bulkMerchant));
            }
            bankMerchantSearchIndexRepository.deleteByMerchantIdIn(notActiveMerchantIds);
        } catch (DataAccessException e) {
            log.error("Removing inactive merchants from db failed.");
        }
    }

    /**
     * This method is used to get inactive bank or merchant ids.
     *
     * @param businessResponseMap businessResponseMap
     * @return inactive bank or merchant ids.
     */
    private List<String> getInactiveBankOrMerchantIds(Map<String, MerchantBusinessResponseDto> businessResponseMap) {
        List<String> notActiveUserIds = new ArrayList<>();
        for (MerchantBusinessResponseDto businessResponseDto : businessResponseMap.values()) {
            if (!businessResponseDto.isActive())
                notActiveUserIds.add(businessResponseDto.getId());
        }
        return notActiveUserIds;
    }
}
