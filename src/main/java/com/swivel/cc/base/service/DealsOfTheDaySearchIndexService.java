package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.DealOfTheDaySearchIndex;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.BasicDealFilterType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.DealsOfTheDaySearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * This class should replace from a search engine.
 */
@Service
public class DealsOfTheDaySearchIndexService {

    private static final String ALL = "ALL";
    private static final String MERCHANT = "MERCHANT";
    private static final String CATEGORY = "CATEGORY";
    private static final String SEARCH = "SEARCH";
    private static final String BIND = "_";
    private final DealsOfTheDaySearchIndexRepository dealsOfTheDaySearchIndexRepository;

    @Autowired
    public DealsOfTheDaySearchIndexService(DealsOfTheDaySearchIndexRepository dealsOfTheDaySearchIndexRepository) {
        this.dealsOfTheDaySearchIndexRepository = dealsOfTheDaySearchIndexRepository;
    }

    /**
     * Delete all deals of the day search index
     */
    public void deleteAll() {
        try {
            dealsOfTheDaySearchIndexRepository.deleteAll();
        } catch (DataAccessException e) {
            throw new QponCoreException("Deleting all deals of the day search index from database was failed", e);
        }
    }

    /**
     * Save all deals of the day search indexes
     *
     * @param dealOfTheDaySearchIndexList dealOfTheDaySearchIndexList
     */
    public void saveAll(List<DealOfTheDaySearchIndex> dealOfTheDaySearchIndexList) {
        try {
            dealsOfTheDaySearchIndexRepository.saveAll(dealOfTheDaySearchIndexList);
        } catch (DataAccessException e) {
            throw new QponCoreException("Save all deals of the day search index to database was failed", e);
        }
    }


    /**
     * Get all searchDeals for Merchant/ Bank
     *
     * @param pageable   pageable
     * @param categoryId categoryId / ALL
     * @param merchantId merchantId / ALL
     * @param searchTerm searchTerm / ALL
     * @return DealSearchIndex Page
     */
    public Page<DealOfTheDaySearchIndex> getAllSearchDeals(Pageable pageable, String categoryId, String merchantId,
                                                           String searchTerm, UserType dealSource) {
        try {
            var dealFilterType = createFilterType(categoryId, merchantId, searchTerm);
            switch (dealFilterType) {
                case CATEGORY_MERCHANT_SEARCH:
                    return dealsOfTheDaySearchIndexRepository
                            .findAllByMerchantIdCategoryIdSearchTermAndDealSource(pageable,
                                    merchantId, categoryId, searchTerm, dealSource);
                case ALL_MERCHANT_SEARCH:
                    return dealsOfTheDaySearchIndexRepository
                            .findAllByMerchantIdAndSearchTermAndDealSource(pageable, merchantId, searchTerm, dealSource);
                case CATEGORY_MERCHANT_ALL:
                    return dealsOfTheDaySearchIndexRepository
                            .findAllByMerchantIdAndCategoryIdAndDealSource(pageable, merchantId, categoryId, dealSource);
                case CATEGORY_ALL_SEARCH:
                    return dealsOfTheDaySearchIndexRepository
                            .findAllByCategoryIdAndSearchTermAndDealSource(pageable, categoryId, searchTerm, dealSource);
                case CATEGORY_ALL_ALL:
                    return dealsOfTheDaySearchIndexRepository.findAllByCategoryIdAndDealSource(pageable, categoryId, dealSource);
                case ALL_MERCHANT_ALL:
                    return dealsOfTheDaySearchIndexRepository.findAllByMerchantIdAndDealSource(pageable, merchantId, dealSource);
                case ALL_ALL_SEARCH:
                    return dealsOfTheDaySearchIndexRepository.findAllApprovedDealsBySearchTermAndDealSource(pageable,
                            searchTerm, dealSource);
                default:
                    return dealsOfTheDaySearchIndexRepository.findAllSearchDealsByDealSource(pageable, dealSource);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading dealSearchIndex from the database was failed.", e);
        }
    }

    /**
     * This method combine categoryId, merchantId and searchTerm and returns enum
     *
     * @param categoryId categoryId / ALL
     * @param merchantId merchantId / ALL
     * @param searchTerm searchTerm / ALL
     * @return DealFilterType
     */
    private BasicDealFilterType createFilterType(String categoryId, String merchantId, String searchTerm) {
        String part1 = categoryId.equals(ALL) ? ALL : CATEGORY;
        String part2 = merchantId.equals(ALL) ? ALL : MERCHANT;
        String part3 = searchTerm.equals(ALL) ? ALL : SEARCH;

        var value = part1 + BIND + part2 + BIND + part3;

        return BasicDealFilterType.valueOf(value);
    }

    /**
     * This method will update merchant info.
     *
     * @param merchantIds merchant ids List
     * @param merchantMap merchant id, MerchantBusinessResponseDto Map
     */
    public void updateMerchantInfo(List<String> merchantIds, Map<String, MerchantBusinessResponseDto> merchantMap) {
        try {
            var dealOfTheDaySearchIndexList =
                    dealsOfTheDaySearchIndexRepository.getAllByMerchantIdIn(merchantIds);
            for (var dealOfTheDaySearchIndex : dealOfTheDaySearchIndexList) {
                var merchant = merchantMap.get(dealOfTheDaySearchIndex.getMerchantId());
                dealOfTheDaySearchIndex.setMerchantName(merchant.getName());
                dealOfTheDaySearchIndex.setMerchantImageUrl(merchant.getImageUrl());
                dealOfTheDaySearchIndex.setActiveMerchant(merchant.isActive());
            }
            dealsOfTheDaySearchIndexRepository.saveAll(dealOfTheDaySearchIndexList);
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating merchant info for dealOfTheDaySearchIndex was failed", e);
        }
    }
}
