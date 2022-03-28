package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchant;
import com.swivel.cc.base.domain.response.*;
import com.swivel.cc.base.enums.CategoryType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Optional;

/**
 * Summary service
 */
@Service
public class SummaryService {

    private static final int ZERO = 0;
    private final DealRepository dealRepository;
    private final RequestADealRepository requestADealRepository;
    private final BrandRepository brandRepository;
    private final CategoryRepository categoryRepository;
    private final DealSearchIndexRepository dealSearchIndexRepository;
    private final AuthUserService authUserService;
    private final CategoryBrandMerchantRepository categoryBrandMerchantRepository;
    private final CreditCardRepository creditCardRepository;
    private final BankDealRepository bankDealRepository;

    @Autowired
    public SummaryService(DealRepository dealRepository, RequestADealRepository requestADealRepository,
                          BrandRepository brandRepository, CategoryRepository categoryRepository,
                          DealSearchIndexRepository dealSearchIndexRepository, AuthUserService authUserService,
                          CategoryBrandMerchantRepository categoryBrandMerchantRepository,
                          CreditCardRepository creditCardRepository, BankDealRepository bankDealRepository) {
        this.dealRepository = dealRepository;
        this.requestADealRepository = requestADealRepository;
        this.brandRepository = brandRepository;
        this.categoryRepository = categoryRepository;
        this.dealSearchIndexRepository = dealSearchIndexRepository;
        this.authUserService = authUserService;
        this.categoryBrandMerchantRepository = categoryBrandMerchantRepository;
        this.creditCardRepository = creditCardRepository;
        this.bankDealRepository = bankDealRepository;
    }

    /**
     * This method is used to get full summary of admin/merchant.
     *
     * @param timeZone  timeZone
     * @param roleType  roleType
     * @param userId    userId
     * @param authToken authToken
     * @return full summary.
     */
    public FullSummaryResponseDto getFullSummary(String timeZone, UserType roleType, String userId, String authToken) {
        TodaySummaryResponseDto todaySummaryResponseDto = getTodaySummary(timeZone, roleType, userId);
        SummaryResponseDto summaryResponseDto = null;
        TodayAuthSummaryResponseDto todayAuthSummaryResponseDto;
        BusinessMerchantResponseDto businessProfile;

        switch (roleType) {
            case ADMIN:
                todayAuthSummaryResponseDto =
                        authUserService.getTodaySummaryResponse(userId, authToken, roleType.toString());
                todaySummaryResponseDto.setAdminResponse(todayAuthSummaryResponseDto);
                summaryResponseDto = getSummaryForAdmin();
                summaryResponseDto.setAdminResponse(todayAuthSummaryResponseDto);
                break;
            case BANK:
                todayAuthSummaryResponseDto =
                        authUserService.getTodaySummaryResponse(userId, authToken, roleType.toString());
                todaySummaryResponseDto.setBankResponse(todayAuthSummaryResponseDto);
                summaryResponseDto = getSummaryForMerchantOrBank(roleType, userId);
                businessProfile =
                        authUserService.getBankBusinessByBankId(authToken, userId);
                summaryResponseDto.setTotalNoOfCreditCardRequest(creditCardRepository.countAllByBankId(userId));
                summaryResponseDto.setTitleAndSubTitleResponse(businessProfile.getCreatedAt().getDisplayDate(), timeZone);
                break;
            case MERCHANT:
                businessProfile =
                        authUserService.getMerchantBusinessByMerchantId(authToken, userId);
                summaryResponseDto = getSummaryForMerchantOrBank(roleType, userId);
                summaryResponseDto.setTitleAndSubTitleResponse(businessProfile.getCreatedAt().getDisplayDate(), timeZone);
                break;
        }
        return new FullSummaryResponseDto(todaySummaryResponseDto, summaryResponseDto);
    }

    /**
     * This method is used to get today's summary.
     *
     * @param timeZone timeZone
     * @param roleType roleType
     * @param userId   userId
     * @return today summary response
     */
    private TodaySummaryResponseDto getTodaySummary(String timeZone, UserType roleType, String userId) {
        LocalDate today = LocalDate.now(ZoneId.of(timeZone));
        long todayInMillis = today.atStartOfDay(ZoneId.of(timeZone)).toInstant().toEpochMilli();
        long tomorrowInMillis = today.plusDays(1).atStartOfDay(ZoneId.of(timeZone)).toInstant().toEpochMilli();
        TodaySummaryResponseDto todaySummaryResponseDto = new TodaySummaryResponseDto();

        switch (roleType) {
            case ADMIN:
                todaySummaryResponseDto = getTodaySummaryForAdmin(todayInMillis, tomorrowInMillis);
                break;
            case MERCHANT:
                todaySummaryResponseDto = getTodaySummaryForMerchant(todayInMillis, tomorrowInMillis, userId);
                break;
            case BANK:
                todaySummaryResponseDto = getTodaySummaryForBank(todayInMillis, tomorrowInMillis, userId);
                break;
        }
        todaySummaryResponseDto.setSubTitle(todayInMillis, timeZone, Date.valueOf(today));
        return todaySummaryResponseDto;
    }

    /**
     * This method is used to get today's summary for admin.
     *
     * @param todayInMillis    todayInMillis
     * @param tomorrowInMillis tomorrowInMillis
     * @return today summary for admin.
     */
    private TodaySummaryResponseDto getTodaySummaryForAdmin(long todayInMillis, long tomorrowInMillis) {
        long noOfNewDeals =
                dealRepository.countByCreatedAtGreaterThanEqual(todayInMillis) +
                        bankDealRepository.countByCreatedAtGreaterThanEqual(todayInMillis);
        long noOfExpiringDeals =
                dealRepository.countByExpiredOnGreaterThanEqualAndExpiredOnLessThan(todayInMillis, tomorrowInMillis) +
                        bankDealRepository.countByExpiredOnGreaterThanEqualAndExpiredOnLessThan(todayInMillis, tomorrowInMillis);
        long noOfNewDealRequests = requestADealRepository.countByCreatedAtGreaterThanEqual(todayInMillis);
        Long newCreditCardRequests = creditCardRepository.countByCreatedAtGreaterThanEqual(todayInMillis);

        return new TodaySummaryResponseDto(noOfNewDeals, noOfExpiringDeals, noOfNewDealRequests, newCreditCardRequests);
    }

    /**
     * This method is used to get today's summary for merchant.
     *
     * @param todayInMillis    todayInMillis
     * @param tomorrowInMillis tomorrowInMillis
     * @param userId           userId
     * @return today summary for merchant.
     */
    private TodaySummaryResponseDto getTodaySummaryForMerchant(long todayInMillis, long tomorrowInMillis, String userId) {
        long noOfNewDeals = dealRepository.countByMerchantIdAndCreatedAtGreaterThanEqual(userId, todayInMillis);
        long noOfExpiringDeals =
                dealRepository.countByMerchantIdAndExpiredOnGreaterThanEqualAndExpiredOnLessThan(userId,
                        todayInMillis, tomorrowInMillis);
        long noOfNewDealRequests = requestADealRepository
                .countByMerchantIdAndCreatedAtGreaterThanEqual(userId, todayInMillis);

        return new TodaySummaryResponseDto(noOfNewDeals, noOfExpiringDeals, noOfNewDealRequests, null);
    }

    /**
     * This method is used to get today's summary for bank.
     *
     * @param todayInMillis    todayInMillis
     * @param tomorrowInMillis tomorrowInMillis
     * @param userId           userId
     * @return today summary for bank.
     */
    private TodaySummaryResponseDto getTodaySummaryForBank(long todayInMillis, long tomorrowInMillis, String userId) {
        long noOfNewDeals = bankDealRepository.countByBankIdAndCreatedAtGreaterThanEqual(userId, todayInMillis);
        long noOfExpiringDeals =
                bankDealRepository.countByBankIdAndExpiredOnGreaterThanEqualAndExpiredOnLessThan(userId,
                        todayInMillis, tomorrowInMillis);
        long noOfNewDealRequests = requestADealRepository
                .countByMerchantIdAndCreatedAtGreaterThanEqual(userId, todayInMillis);
        Long newCreditCardRequests = creditCardRepository.countByBankIdAndCreatedAtGreaterThanEqual(userId, todayInMillis);

        return new TodaySummaryResponseDto(noOfNewDeals, noOfExpiringDeals, noOfNewDealRequests, newCreditCardRequests);
    }

    /**
     * This method is used to get summary for admin.
     *
     * @return summary response for admin.
     */
    private SummaryResponseDto getSummaryForAdmin() {
        long currentTimeInMillis = System.currentTimeMillis();
        long totalCategories = categoryRepository.count();
        long totalActiveCategories = categoryRepository
                .countByExpiryDateIsNullOrExpiryDateGreaterThan(currentTimeInMillis);
        long totalBrands = brandRepository.count();
        long totalBankDeals = bankDealRepository.count();
        long totalMerchantDeals = dealRepository.count();
        long totalDeals = totalMerchantDeals + totalBankDeals;
        long totalActiveDeals = dealSearchIndexRepository.getTotalNumberOfActiveDeals(currentTimeInMillis);
        long totalNoOfDealRequests = requestADealRepository.count();

        SummaryResponseDto summaryResponseDto = new SummaryResponseDto(totalCategories, totalActiveCategories,
                totalBrands, totalDeals, totalActiveDeals, totalNoOfDealRequests);
        summaryResponseDto.setTotalBankDeals(totalBankDeals);
        summaryResponseDto.setTotalMerchantDeals(totalMerchantDeals);
        summaryResponseDto.setTotalNoOfCreditCardRequest(creditCardRepository.count());
        return summaryResponseDto;
    }

    /**
     * This method is used to get summary for merchant/bank.
     *
     * @param roleType merchant/bank
     * @param userId   userId
     * @return summary response for merchant/bank.
     */
    private SummaryResponseDto getSummaryForMerchantOrBank(UserType roleType, String userId) {
        long currentTimeInMillis = System.currentTimeMillis();
        Optional<CategoryBrandMerchant> optionalCategoryBrandMerchant =
                categoryBrandMerchantRepository.findByMerchantId(userId);
        long totalCategories = getTotalCategoriesForMerchantId(optionalCategoryBrandMerchant);
        long totalActiveCategories = getTotalActiveCategoriesForMerchantId(optionalCategoryBrandMerchant);
        long totalBrands = getTotalBrandsForMerchantId(optionalCategoryBrandMerchant);
        long totalActiveDeals =
                dealSearchIndexRepository.getTotalNumberOfActiveDealsForMerchantId(userId, currentTimeInMillis);
        long totalNoOfDealRequests = requestADealRepository.countByMerchantId(userId);
        long totalDeals = roleType.equals(UserType.MERCHANT) ? dealRepository.countByMerchantId(userId) :
                bankDealRepository.countByBankId(userId);
        return new SummaryResponseDto(totalCategories, totalActiveCategories, totalBrands, totalDeals,
                totalActiveDeals, totalNoOfDealRequests);
    }

    /**
     * Used to get total categories for merchantId.
     *
     * @param optionalCategoryBrandMerchant optionalCategoryBrandMerchant
     * @return total number of categories for merchant.
     */
    private int getTotalCategoriesForMerchantId(Optional<CategoryBrandMerchant> optionalCategoryBrandMerchant) {
        return optionalCategoryBrandMerchant.map(categoryBrandMerchant ->
                categoryBrandMerchant.getCategories().size()).orElse(ZERO);
    }

    /**
     * Used to get total brands for merchantId.
     *
     * @param optionalCategoryBrandMerchant optionalCategoryBrandMerchant
     * @return total number of brands for merchant.
     */
    private int getTotalBrandsForMerchantId(Optional<CategoryBrandMerchant> optionalCategoryBrandMerchant) {
        return optionalCategoryBrandMerchant.map(categoryBrandMerchant ->
                categoryBrandMerchant.getBrands().size()).orElse(ZERO);
    }

    /**
     * Used to get total active categories for merchantId.
     *
     * @param optionalCategoryBrandMerchant optionalCategoryBrandMerchant
     * @return total number of active categories for merchant.
     */
    private int getTotalActiveCategoriesForMerchantId(Optional<CategoryBrandMerchant> optionalCategoryBrandMerchant) {
        long currentTimeInMillis = System.currentTimeMillis();
        int activeCategoryCount = ZERO;
        if (optionalCategoryBrandMerchant.isPresent()) {
            for (String categoryId : optionalCategoryBrandMerchant.get().getCategories()) {
                Optional<Category> optionalCategory = categoryRepository.findById(categoryId);
                if (optionalCategory.isPresent()) {
                    Category category = optionalCategory.get();
                    CategoryType categoryType = category.getCategoryType();
                    Long expiryDate = category.getExpiryDate();
                    if (categoryType == CategoryType.NORMAL || (expiryDate != null && expiryDate > currentTimeInMillis)) {
                        activeCategoryCount++;
                    }
                }
            }
        }
        return activeCategoryCount;
    }
}
