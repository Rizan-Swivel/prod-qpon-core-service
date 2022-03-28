package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.entity.DealSearchIndex;
import com.swivel.cc.base.domain.request.DealApprovalStatusUpdateRequestDto;
import com.swivel.cc.base.domain.request.DealRequestDto;
import com.swivel.cc.base.domain.request.DealUpdateRequestDto;
import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.domain.response.DealViewCountResponse;
import com.swivel.cc.base.domain.response.ViewCountAnalyticResponseDto;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidDealException;
import com.swivel.cc.base.exception.InvalidUserException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.BankDealRepository;
import com.swivel.cc.base.repository.DealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;


@Service
public class DealService {

    private static final String INVALID_DEAL = "Invalid deal Id: ";
    private static final String READ_DEAL = "Reading deal from the database was failed.";

    private final DealRepository dealRepository;
    private final BankDealRepository bankDealRepository;
    private final DealSearchService dealSearchService;
    private final DealApprovalService dealApprovalService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final MerchantBankSearchIndexService merchantBankSearchIndexService;

    @Autowired
    public DealService(DealRepository dealRepository, BankDealRepository bankDealRepository,
                       DealSearchService dealSearchService, DealApprovalService dealApprovalService,
                       BrandService brandService, CategoryService categoryService,
                       MerchantBankSearchIndexService merchantBankSearchIndexService, DealCodeService dealCodeService) {
        this.dealRepository = dealRepository;
        this.bankDealRepository = bankDealRepository;
        this.dealSearchService = dealSearchService;
        this.dealApprovalService = dealApprovalService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.merchantBankSearchIndexService = merchantBankSearchIndexService;
    }

    /**
     * Create deal
     *
     * @param deal deal
     * @return deal
     */
    public void createDeal(Deal deal, BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto,
                           UserType userType, DealRequestDto dealRequestDto, BusinessMerchantResponseDto merchant) {
        try {
            if (userType == UserType.MERCHANT) {
                dealRepository.save(deal);
            } else {
                BankDeal bankDeal = new BankDeal(deal);
                bankDeal.setMerchantId(dealRequestDto.getShopId());
                bankDealRepository.save(bankDeal);
                merchantBankSearchIndexService.save(bankDeal, basicMerchantBusinessResponseDto, merchant);
            }
            var filterDeal = new DealSearchIndex(deal, basicMerchantBusinessResponseDto, userType);
            dealSearchService.save(filterDeal);
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving deal to database was failed", e);
        }
    }

    /**
     * Update deal delete flag
     *
     * @param userType Merchant/Bank
     * @param dealId   deal Id
     */
    public void deleteDeal(String dealId, UserType userType) {
        try {
            Optional<Deal> optionalDealFromDb;
            if (userType.equals(UserType.MERCHANT))
                optionalDealFromDb = dealRepository.findPendingDealById(dealId);
            else
                optionalDealFromDb = bankDealRepository.findPendingDealById(dealId);

            if (optionalDealFromDb.isPresent()) {
                var dealFromDb = optionalDealFromDb.get();
                dealFromDb.setDeleted(true);
                dealFromDb.setUpdatedAt(System.currentTimeMillis());
                if (userType.equals(UserType.MERCHANT))
                    dealRepository.save(dealFromDb);
                else
                    bankDealRepository.save(new BankDeal(dealFromDb));
                dealSearchService.deleteDeal(dealId);
            } else
                throw new InvalidDealException(INVALID_DEAL + dealId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Read/Write deal from database was failed.", e);
        }
    }

    /**
     * Approve the deal
     *
     * @param dealApprovalStatusUpdateRequestDto dealStatusUpdateRequestDto
     */
    public Deal updateApprovalOfDeal(DealApprovalStatusUpdateRequestDto dealApprovalStatusUpdateRequestDto,
                                     String authToken, UserType userType) {
        try {
            Optional<Deal> optionalDealFromDb = userType.equals(UserType.MERCHANT) ?
                    dealRepository.findPendingDealById(dealApprovalStatusUpdateRequestDto.getId()) :
                    bankDealRepository.findPendingDealById(dealApprovalStatusUpdateRequestDto.getId());
            if (optionalDealFromDb.isPresent()) {
                var dealFromDb = optionalDealFromDb.get();
                dealFromDb.setApprovalStatus(dealApprovalStatusUpdateRequestDto.getApprovalStatus());
                dealFromDb.setComment(dealApprovalStatusUpdateRequestDto.getComment());
                dealFromDb.setUpdatedAt(System.currentTimeMillis());
                if (userType.equals(UserType.MERCHANT))
                    dealRepository.save(dealFromDb);
                else
                    bankDealRepository.save(new BankDeal(dealFromDb));
                dealSearchService.updateApprovalStatus(dealApprovalStatusUpdateRequestDto);
                dealApprovalService.sendMailAndSmsToMerchant(dealFromDb, dealApprovalStatusUpdateRequestDto, authToken);
                return dealFromDb;
            } else
                throw new InvalidDealException(INVALID_DEAL + dealApprovalStatusUpdateRequestDto.getId());
        } catch (DataAccessException e) {
            throw new QponCoreException("Read/Write deal from database was failed.", e);
        }
    }

    /**
     * Get deal list page wise by searching
     *
     * @param pageable pageable
     * @return page
     */
    public Page<Deal> listAllDeals(Pageable pageable) {
        try {
            return dealRepository.findAllDeals(pageable);
        } catch (DataAccessException e) {
            throw new QponCoreException("Read deals from database was failed.", e);
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
            return dealSearchService.searchDeals(pageable, merchantId, searchTerm, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Read deals from database was failed.", e);
        }
    }

    /**
     * Get PENDING deal list page - ALL / searchTerm
     *
     * @param pageable pageable
     * @return page
     */
    public Page<DealSearchIndex> getPendingDeals(Pageable pageable, String searchTerm, UserType userType) {
        try {
            return dealSearchService.getPendingSearchDeals(pageable, searchTerm, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Read pending deals from database was failed.", e);
        }
    }

    /**
     * Get all searchDeal list by category id, merchant id, brand id and search term.
     *
     * @param pageable pageable
     * @return page dealSearchIndex
     */
    public Page<DealSearchIndex> getAllSearchDeals(Pageable pageable, String categoryId,
                                                   String merchantId, String brandId, String searchTerm,
                                                   boolean onlyActiveMerchant, boolean onlyActiveDeals, UserType userType) {

        return (onlyActiveMerchant && onlyActiveDeals) ? dealSearchService
                .getAllActiveDealsForActiveMerchant(pageable, categoryId, merchantId, brandId, searchTerm, userType) :
                dealSearchService.getAllSearchDeals(pageable, categoryId, merchantId, brandId, searchTerm, userType.name());

    }

    /**
     * Get all recently expiring deals list page.
     *
     * @param pageable   pageable
     * @param categoryId category id
     * @param merchantId merchant id
     * @param brandId    brand id
     * @param searchTerm search term
     * @return DealSearchIndex page
     */
    public Page<DealSearchIndex> getRecentlyExpireDeals(Pageable pageable, String categoryId,
                                                        String merchantId, String brandId, String searchTerm, UserType userType) {
        try {
            return dealSearchService
                    .getAllSearchRecentlyExpireDeals(pageable, categoryId, merchantId, brandId, searchTerm, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading dealSearchIndex from the database was failed.", e);
        }
    }

    /**
     * This method read deal page by merchant Id
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @return page
     */
    public Page<Deal> listAllDealsByMerchant(Pageable pageable, String merchantId) {
        try {
            return dealRepository.findAllDealsByMerchantId(pageable, merchantId);
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_DEAL, e);
        }
    }

    /**
     * Get deal by Id
     *
     * @param dealId dealId
     * @return deal
     */
    public Deal getDeal(String dealId, UserType userType) {
        try {
            Optional<Deal> optionalDealFromDb;
            if (userType.equals(UserType.MERCHANT))
                optionalDealFromDb = dealRepository.findDealById(dealId);
            else {
                Optional<BankDeal> optionalBankDeal = bankDealRepository.findDealById(dealId);
                optionalDealFromDb = optionalBankDeal.map(Deal::new);
            }
            if (optionalDealFromDb.isPresent()) {
                return optionalDealFromDb.get();
            } else {
                throw new InvalidDealException(INVALID_DEAL + dealId);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_DEAL, e);
        }
    }

    /**
     * This method get deals list by deal id.
     *
     * @param dealViewsList dealViewsList
     * @return list of deals with view count & display date.
     */
    public List<DealViewCountResponse> getDealSetByIds(List<ViewCountAnalyticResponseDto> dealViewsList) {
        try {
            List<DealViewCountResponse> dealsAndViews = new ArrayList<>();
            for (ViewCountAnalyticResponseDto dealIdAndViews : dealViewsList) {
                Optional<Deal> optionalDealFromDb = dealRepository.findById(dealIdAndViews.getDealId());
                optionalDealFromDb.ifPresent(deal -> dealsAndViews.add(
                        new DealViewCountResponse(deal, dealIdAndViews.getMerchantId(),
                                dealIdAndViews.getViewCount(), dealIdAndViews.getDisplayDate())));
            }
            return dealsAndViews;
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading deal by id from database was failed.", e);
        }
    }

    /**
     * This method update an existing deal and deal search index.
     *
     * @param dealUpdateRequestDto        dealUpdateRequestDto
     * @param userType                    Merchant/Bank
     * @param deal                        deal From DB
     * @param businessMerchantResponseDto businessMerchantResponseDto
     * @return deal
     */
    public Deal updateDeal(DealUpdateRequestDto dealUpdateRequestDto, UserType userType, Deal deal,
                           BusinessMerchantResponseDto businessMerchantResponseDto) {
        try {
            if (!userType.toString().equalsIgnoreCase(businessMerchantResponseDto.getProfileType()))
                throw new InvalidUserException("User type doesn't match with user id.");
            var categoriesList = categoryService.getCategorySetByIds(dealUpdateRequestDto.getCategoryIds());
            Set<Brand> brandsList = new HashSet<>();
            deal.setRelatedCategories(categoriesList);
            if (dealUpdateRequestDto.isBrandIdsAvailable()) {
                brandsList.addAll(brandService.getBrandSetByIdList(dealUpdateRequestDto.getBrandIds()));
                deal.setRelatedBrands(brandsList);
            }
            deal.update(dealUpdateRequestDto, categoriesList, brandsList);
            dealSearchService.updateDealSearchIndex(deal, businessMerchantResponseDto);
            if (userType.equals(UserType.MERCHANT))
                dealRepository.save(deal);
            else
                bankDealRepository.save(new BankDeal(deal));
            return deal;
        } catch (DataAccessException e) {
            throw new QponCoreException("Update deal was failed.", e);
        }
    }

    /**
     * This method returns active bank deals for active bankId & merchantId.
     *
     * @param pageable   pageable
     * @param bankId     bankId
     * @param merchantId merchantId
     * @return bank deals page.
     */
    public Page<BankDeal> getActiveDealsForActiveBankAndMerchant(Pageable pageable, String bankId, String merchantId) {
        try {
            List<String> dealIdList = dealSearchService.getActiveDealsForActiveBank(bankId);
            return bankDealRepository.findByMerchantIdAndIdIn(pageable, merchantId, dealIdList);
        } catch (DataAccessException e) {
            throw new QponCoreException("Getting active deals for active bank & merchant was failed.", e);
        }
    }

    /**
     * This method returns true when bank deal's expiredOn equal or less than current mills.
     *
     * @param dealId deal id
     * @return true/ false
     */
    public boolean checkDealIsExpired(String dealId) {
        try {
            return bankDealRepository.isBankDealExpired(dealId, System.currentTimeMillis());
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading BankDeal by id was failed.", e);
        }
    }
}
