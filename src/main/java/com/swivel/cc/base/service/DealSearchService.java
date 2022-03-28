package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.entity.DealSearchIndex;
import com.swivel.cc.base.domain.request.DealApprovalStatusUpdateRequestDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.DealFilterType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidDealException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.DealSearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * This class should replace from a search engine.
 */
@Service
public class DealSearchService {

    private static final String INVALID_DEAL_ID = "Invalid deal Id:";
    private static final String READ_FILTER_DEAL = "Reading deal from search index was failed.";
    private static final String ALL = "ALL";
    private static final String MERCHANT = "MERCHANT";
    private static final String CATEGORY = "CATEGORY";
    private static final String BRAND = "BRAND";
    private static final String SEARCH = "SEARCH";
    private static final String BIND = "_";


    private final DealSearchIndexRepository dealSearchIndexRepository;


    @Autowired
    public DealSearchService(DealSearchIndexRepository dealSearchIndexRepository) {
        this.dealSearchIndexRepository = dealSearchIndexRepository;
    }

    /**
     * Create deals search index
     *
     * @param dealSearchIndex dealSearchIndex
     */
    public void save(DealSearchIndex dealSearchIndex) {
        try {
            dealSearchIndexRepository.save(dealSearchIndex);
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving deals search index to database was failed", e);
        }
    }

    /**
     * Find by Id
     *
     * @param dealId dealId
     * @return DealSearchIndex
     */
    public DealSearchIndex findById(String dealId) {
        try {
            Optional<DealSearchIndex> optionalDealFromDb = dealSearchIndexRepository.findById(dealId);

            if (optionalDealFromDb.isPresent()) {
                return optionalDealFromDb.get();
            } else {
                throw new InvalidDealException(INVALID_DEAL_ID + dealId);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving deals search index to database was failed", e);
        }
    }

    /**
     * Delete filter deal
     *
     * @param dealId deal Id
     */
    public void deleteDeal(String dealId) {
        try {
            Optional<DealSearchIndex> optionalDealFromDb = dealSearchIndexRepository.findPendingDealById(dealId);

            if (optionalDealFromDb.isPresent()) {
                var filterDealFromDb = optionalDealFromDb.get();
                filterDealFromDb.setDeleted(true);
                dealSearchIndexRepository.save(filterDealFromDb);
            } else
                throw new InvalidDealException(INVALID_DEAL_ID + dealId);
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_FILTER_DEAL, e);
        }
    }

    /**
     * Approve the filter deal
     *
     * @param dealApprovalStatusUpdateRequestDto dealStatusUpdateRequestDto
     */
    public void updateApprovalStatus(DealApprovalStatusUpdateRequestDto dealApprovalStatusUpdateRequestDto) {
        try {
            Optional<DealSearchIndex> optionalDealFromDb = dealSearchIndexRepository.
                    findPendingDealById(dealApprovalStatusUpdateRequestDto.getId());

            if (optionalDealFromDb.isPresent()) {
                var filterDealFromDb = optionalDealFromDb.get();
                filterDealFromDb.setApprovalStatus(dealApprovalStatusUpdateRequestDto.getApprovalStatus());
                filterDealFromDb.setComment(dealApprovalStatusUpdateRequestDto.getComment());
                filterDealFromDb.setUpdatedAt(System.currentTimeMillis());
                dealSearchIndexRepository.save(filterDealFromDb);
            } else
                throw new InvalidDealException(INVALID_DEAL_ID + dealApprovalStatusUpdateRequestDto.getId());
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_FILTER_DEAL, e);
        }
    }

    /**
     * Get filter deal list page wise by searching
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @param userType   Merchant/Bank
     * @return page
     */
    public Page<DealSearchIndex> searchDeals(Pageable pageable, String merchantId, String searchTerm, UserType userType) {
        try {
            if (merchantId.equals(ALL) && searchTerm.equals(ALL)) {
                return dealSearchIndexRepository.findAllNotDeletedDeals(pageable, userType);
            } else if (merchantId.equals(ALL)) {
                return dealSearchIndexRepository.findAllNotDeletedDealsBySearchTerm(pageable, searchTerm, userType);
            } else if (searchTerm.equals(ALL)) {
                return dealSearchIndexRepository.findAllNotDeletedDealsByMerchantId(pageable, merchantId);
            }
            return dealSearchIndexRepository
                    .findAllNotDeletedDealsByMerchantIdAndSearchTerm(pageable, merchantId, searchTerm);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading all dealSearchIndexes from the database was failed.", e);
        }
    }

    /**
     * Get PENDING deal list page
     *
     * @param pageable pageable
     * @return page
     */
    public Page<DealSearchIndex> getPendingSearchDeals(Pageable pageable, String searchTerm, UserType userType) {
        try {
            if (searchTerm.equals(ALL))
                return dealSearchIndexRepository.findAllByApprovalStatusAndDealSourceAndIsDeletedFalse(pageable,
                        ApprovalStatus.PENDING, userType);
            else
                return dealSearchIndexRepository.findAllPendingDealsBySearchTerm(pageable, searchTerm, userType.toString());
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading pending dealSearchIndexes from the database was failed.", e);
        }
    }

    /**
     * Get all searchDeals
     *
     * @param pageable   pageable
     * @param categoryId <categoryId/ALL>
     * @param merchantId <merchantId/ALL>
     * @param searchTerm <searchTerm/ALL>
     * @return DealSearchIndex Page
     */
    public Page<DealSearchIndex> getAllSearchDeals(Pageable pageable, String categoryId,
                                                   String merchantId, String brandId, String searchTerm, String dealSource) {
        try {
            var dealFilterType = createFilterType(categoryId, merchantId, brandId, searchTerm);
            switch (dealFilterType) {
                case CATEGORY_MERCHANT_ALL_SEARCH:
                    return dealSearchIndexRepository.findAllByMerchantIdCategoryIdSearchTerm(pageable,
                            merchantId, categoryId, searchTerm, dealSource);
                case ALL_MERCHANT_ALL_SEARCH:
                    return dealSearchIndexRepository.findAllByMerchantIdAndSearchTerm(pageable, merchantId, searchTerm, dealSource);
                case CATEGORY_MERCHANT_ALL_ALL:
                    return dealSearchIndexRepository.findAllByMerchantIdAndCategoryId(pageable, merchantId, categoryId, dealSource);
                case CATEGORY_ALL_ALL_SEARCH:
                    return dealSearchIndexRepository.findAllByCategoryIdAndSearchTerm(pageable, categoryId, searchTerm, dealSource);
                case CATEGORY_ALL_ALL_ALL:
                    return dealSearchIndexRepository.findAllByCategoryId(pageable, categoryId, dealSource);
                case ALL_MERCHANT_ALL_ALL:
                    return dealSearchIndexRepository.findAllByMerchantId(pageable, merchantId, dealSource);
                case ALL_ALL_ALL_SEARCH:
                    return dealSearchIndexRepository.findAllApprovedDealsBySearchTerm(pageable, searchTerm, dealSource);
                case ALL_ALL_BRAND_ALL:
                    return dealSearchIndexRepository.findAllByBrandId(pageable, brandId, dealSource);
                case ALL_ALL_BRAND_SEARCH:
                    return dealSearchIndexRepository.findAllByBrandIdAndSearchTerm(pageable, brandId, searchTerm, dealSource);
                default:
                    return dealSearchIndexRepository.findAllSearchDeals(pageable, dealSource);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_FILTER_DEAL, e);
        }
    }

    /**
     * Get All Search Deals for active merchants
     *
     * @param pageable   pageable
     * @param categoryId category id
     * @param merchantId merchant id
     * @param brandId    brand id
     * @param searchTerm search term
     * @return DealSearchIndex page
     **/

    public Page<DealSearchIndex> getAllActiveDealsForActiveMerchant(Pageable pageable, String categoryId, String merchantId,
                                                                    String brandId, String searchTerm, UserType dealSource) {
        try {
            var currentMilliSecond = System.currentTimeMillis();
            var dealFilterType = createFilterType(categoryId, merchantId, brandId, searchTerm);

            switch (dealFilterType) {
                case CATEGORY_MERCHANT_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllActiveDealsByActiveMerchantIdCategoryIdSearchTerm(
                                    pageable, merchantId, categoryId, searchTerm, currentMilliSecond, dealSource);
                case ALL_MERCHANT_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllActiveDealsByActiveMerchantIdAndSearchTerm(
                                    pageable, merchantId, searchTerm, currentMilliSecond, dealSource);
                case CATEGORY_MERCHANT_ALL_ALL:
                    return dealSearchIndexRepository
                            .findAllActiveDealsByActiveMerchantIdAndCategoryId(
                                    pageable, merchantId, categoryId, currentMilliSecond, dealSource);
                case CATEGORY_ALL_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllActiveDealsCategoryIdAndSearchTermAndActiveMerchant(
                                    pageable, categoryId, searchTerm, currentMilliSecond, dealSource);
                case CATEGORY_ALL_ALL_ALL:
                    return dealSearchIndexRepository
                            .findAllActiveDealsByCategoryIdAndActiveMerchant(
                                    pageable, categoryId, currentMilliSecond, dealSource);
                case ALL_MERCHANT_ALL_ALL:
                    return dealSearchIndexRepository
                            .findAllActiveDealsByActiveMerchantId(pageable, merchantId, currentMilliSecond, dealSource);
                case ALL_ALL_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllApprovedActiveDealsBySearchTermAndActiveMerchant(
                                    pageable, searchTerm, currentMilliSecond, dealSource);
                default:
                    return dealSearchIndexRepository.findAllActiveDealsWithActiveMerchant(pageable, currentMilliSecond, dealSource);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_FILTER_DEAL, e);
        }
    }


    /**
     * Get All recently expire deals by category id, merchant id, brand id and search term.
     *
     * @param pageable   pageable
     * @param categoryId category id
     * @param merchantId merchant id
     * @param brandId    brand id
     * @param searchTerm search term
     * @return DealSearchIndex Page
     */
    public Page<DealSearchIndex> getAllSearchRecentlyExpireDeals(Pageable pageable, String categoryId,
                                                                 String merchantId, String brandId,
                                                                 String searchTerm, UserType userType) {
        try {
            var dealFilterType = createFilterType(categoryId, merchantId, brandId, searchTerm);
            var currentMills = System.currentTimeMillis();

            switch (dealFilterType) {
                case CATEGORY_MERCHANT_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllValidDealsByMerchantIdCategoryIdSearchTerm(
                                    pageable, merchantId, categoryId, searchTerm, currentMills, userType);
                case ALL_MERCHANT_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllValidDealsByMerchantIdAndSearchTerm(
                                    pageable, merchantId, searchTerm, currentMills, userType);
                case CATEGORY_MERCHANT_ALL_ALL:
                    return dealSearchIndexRepository
                            .findAllValidDealsByMerchantIdAndCategoryId(
                                    pageable, merchantId, categoryId, currentMills, userType);
                case CATEGORY_ALL_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllValidDealsByCategoryIdAndSearchTerm(
                                    pageable, categoryId, searchTerm, currentMills, userType);
                case CATEGORY_ALL_ALL_ALL:
                    return dealSearchIndexRepository
                            .findAllValidDealsByCategoryId(pageable, categoryId, currentMills, userType);
                case ALL_MERCHANT_ALL_ALL:
                    return dealSearchIndexRepository
                            .findAllValidDealsByMerchantId(pageable, merchantId, currentMills, userType);
                case ALL_ALL_ALL_SEARCH:
                    return dealSearchIndexRepository
                            .findAllValidDealsBySearchTerm(pageable, searchTerm, currentMills, userType);
                default:
                    return dealSearchIndexRepository.findAllValidDeals(pageable, currentMills, userType);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_FILTER_DEAL, e);
        }
    }

    /**
     * This method returns a count of deals for specific category id
     *
     * @param categoryId category id
     * @return count deals Count By CategoryId
     */
    public long getDealsCountByCategoryId(String categoryId) {
        try {
            return dealSearchIndexRepository.countByRelatedCategoriesContainsAndIsDeletedFalse(categoryId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading deal count from the database by category id was failed", e);
        }
    }

    /**
     * This method returns active and valid deal count for a specific category id.
     *
     * @param categoryId category id
     * @return active & valid deal count
     */
    public long getActiveDealsCountByCategoryId(String categoryId) {
        try {
            long currentMillis = System.currentTimeMillis();
            return dealSearchIndexRepository.getActiveValidDealsCountByCategoryId(currentMillis, categoryId);
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_FILTER_DEAL, e);
        }
    }

    /**
     * This method returns count of deal for a specific brand.
     *
     * @param brandId brand Id
     * @return count deals Count By BrandId
     */
    public long getDealsCountByBrandId(String brandId) {
        try {
            return dealSearchIndexRepository.countByRelatedBrandsContainsAndIsDeletedFalse(brandId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading deal count from the database by Brand id was failed", e);
        }
    }

    /**
     * This method returns active and valid deal count for a specific brand id.
     *
     * @param brandId brand id
     * @return active & valid deal count
     */
    public long getActiveDealsCountByBrandId(String brandId) {
        try {
            long currentMillis = System.currentTimeMillis();
            return dealSearchIndexRepository.getActiveValidDealsCountByBrandId(currentMillis, brandId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading dealSearchIndex by brand id from the database was failed.", e);
        }
    }

    /**
     * This method combine categoryId, merchantId and searchTerm and returns enum
     *
     * @param categoryId categoryId / ALL
     * @param merchantId merchantId / ALL
     * @param searchTerm searchTerm / ALL
     * @return DealFilterType String eg: ALL_MERCHANT_ALL_ALL
     */
    private DealFilterType createFilterType(String categoryId, String merchantId, String brandId, String searchTerm) {
        String part1 = categoryId.equals(ALL) ? ALL : CATEGORY;
        String part2 = merchantId.equals(ALL) ? ALL : MERCHANT;
        String part3 = brandId.equals(ALL) ? ALL : BRAND;
        String part4 = searchTerm.equals(ALL) ? ALL : SEARCH;
        var value = part1 + BIND + part2 + BIND + part3 + BIND + part4;

        return DealFilterType.valueOf(value);
    }

    /**
     * This method will update DealSearch Index by deal id.
     *
     * @param deal                        deal
     * @param businessMerchantResponseDto businessMerchantResponseDto
     */
    public void updateDealSearchIndex(Deal deal, BusinessMerchantResponseDto businessMerchantResponseDto) {
        try {
            var dealSearchIndex = findById(deal.getId());
            dealSearchIndex.update(deal, businessMerchantResponseDto);
            save(dealSearchIndex);
        } catch (DataAccessException e) {
            throw new QponCoreException("updating deals search index to database was failed", e);
        }
    }

    /**
     * This method will update merchant info.
     *
     * @param merchantIds merchant ids List
     * @param merchantMap merchant id, MerchantBusinessResponseDto Map
     */
    public void updateMerchantInfo(List<String> merchantIds, Map<String, MerchantBusinessResponseDto> merchantMap) {
        try {
            List<DealSearchIndex> dealSearchIndexList = dealSearchIndexRepository.getAllByMerchantIdIn(merchantIds);
            for (var dealSearchIndex : dealSearchIndexList) {
                var merchant = merchantMap.get(dealSearchIndex.getMerchantId());
                dealSearchIndex.setMerchantName(merchant.getName());
                dealSearchIndex.setMerchantImageUrl(merchant.getImageUrl());
                dealSearchIndex.setApprovalStatus(merchant.getApprovalStatus());
                dealSearchIndex.setActiveMerchant(merchant.isActive());
            }
            dealSearchIndexRepository.saveAll(dealSearchIndexList);
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating merchant info for dealSearchIndex was failed", e);
        }
    }

    /**
     * This method returns active deal ids for active bank.
     *
     * @param bankId bankId
     * @return list of deal ids.
     */
    public List<String> getActiveDealsForActiveBank(String bankId) {
        return dealSearchIndexRepository.getActiveDealsByActiveBankId(bankId, System.currentTimeMillis());
    }
}
