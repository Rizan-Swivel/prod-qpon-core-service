package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.entity.BankMerchantSearchIndex;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchant;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchantIndex;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.request.CategoryBrandMerchantRequestDto;
import com.swivel.cc.base.domain.response.*;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.*;
import com.swivel.cc.base.service.*;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

/**
 * Merchant controller
 */
@CrossOrigin
@Slf4j
@RestController
@Validated
@RequestMapping("api/v1/{userType}")
public class MerchantController extends Controller {
    private final CategoryService categoryService;
    private final BrandService brandService;
    private final CategoryBrandMerchantService categoryBrandMerchantService;
    private final AuthUserService authUserService;
    private final CategoryBrandMerchantIndexService categoryBrandMerchantIndexService;
    private final MerchantBankSearchIndexService merchantBankSearchIndexService;

    public MerchantController(CategoryService categoryService, BrandService brandService, CategoryBrandMerchantService categoryBrandMerchantService, CategoryBrandMerchantIndexService categoryBrandMerchantIndexService, AuthUserService authUserService, Translator translator, MerchantBankSearchIndexService merchantBankSearchIndexService) {
        super(translator);
        this.categoryService = categoryService;
        this.brandService = brandService;
        this.categoryBrandMerchantService = categoryBrandMerchantService;
        this.authUserService = authUserService;
        this.categoryBrandMerchantIndexService = categoryBrandMerchantIndexService;
        this.merchantBankSearchIndexService = merchantBankSearchIndexService;
    }

    /**
     * This method update categories for merchant
     *
     * @param timeZone                        timeZone
     * @param userId                          userId
     * @param categoryBrandMerchantRequestDto categoriesForMerchantRequestDto
     * @return ResponseEntity
     */
    @PutMapping("")
    public ResponseEntity<ResponseWrapper> updateCategoriesAndBrandsForMerchant(
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @PathVariable UserType userType,
            @RequestBody CategoryBrandMerchantRequestDto categoryBrandMerchantRequestDto,
            HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userId, timeZone);
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!categoryBrandMerchantRequestDto.isRequiredAvailable()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            var merchantBusiness = getBusinessProfile(userType, authToken,
                    categoryBrandMerchantRequestDto.getMerchantId());
            final Set<BasicBrandResponseDto> basicBrandResponseDtoSet = new HashSet<>();
            final Set<BasicCategoryResponseDto> basicCategoryResponseDtoSet = new HashSet<>();
            var categorySet = categoryService.getCategorySetByIds(
                    categoryBrandMerchantRequestDto.getCategories());
            categorySet.forEach(category -> basicCategoryResponseDtoSet.add(new BasicCategoryResponseDto(category)));
            if (categoryBrandMerchantRequestDto.isBrandIdsAvailable()) {
                var brandSet = brandService.getBrandSetByIdList(categoryBrandMerchantRequestDto.getBrands());
                brandSet.forEach(brand -> basicBrandResponseDtoSet.add(new BasicBrandResponseDto(brand)));
            }
            var categoryBrandMerchant = new CategoryBrandMerchant(categoryBrandMerchantRequestDto,
                    merchantBusiness.getApprovalStatus(), merchantBusiness.isActive(), userType);
            var updatedCategoriesBrandsOfMerchant =
                    categoryBrandMerchantService.updateCategoriesBrandsOfMerchant(categoryBrandMerchant,
                            new BasicMerchantBusinessResponseDto(merchantBusiness));
            log.debug("Successfully updated mapping of categories and brands for Id: {}, userType: {}. "
                            + "By userId: {} at: {}", categoryBrandMerchantRequestDto.getMerchantId(), userType, userId,
                    updatedCategoriesBrandsOfMerchant.getUpdatedAt());
            return getSuccessResponse(new BrandsAndCategoriesForMerchantResponseDto(updatedCategoriesBrandsOfMerchant,
                            basicCategoryResponseDtoSet, basicBrandResponseDtoSet, timeZone),
                    SuccessResponseStatusType.UPDATE_CATEGORIES_BRANDS_FOR_MERCHANT);
        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {} to mapping categories and brands.", userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (InvalidCategoryException e) {
            log.error("Invalid category Id(s) to mapping categories and brands.", e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (InvalidBrandException e) {
            log.error("Invalid brand Id(s) to mapping categories and brands.", e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            log.error("Updating brands and categories was failed for Id: {}, userType: {}. By userId: {}",
                    categoryBrandMerchantRequestDto.getMerchantId(), userType, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method gets business profile from auth service.
     *
     * @param userType  userType
     * @param authToken authToken
     * @param userId    userId
     * @return merchant/bank business profile.
     */
    private BusinessMerchantResponseDto getBusinessProfile(UserType userType, String authToken, String userId) {
        return userType.equals(UserType.MERCHANT) ?
                authUserService.getMerchantBusinessByMerchantId(authToken, userId) :
                authUserService.getBankBusinessByBankId(authToken, userId);
    }

    /**
     * THis method give categories & brands mapping by merchantId
     *
     * @param userId     userId
     * @param timeZone   time zone
     * @param merchantId merchantId
     * @param request    HttpServletRequest
     * @return ResponseEntity
     */
    @GetMapping("/{merchantId}")
    public ResponseEntity<ResponseWrapper> getCategoriesAndBrandsForMerchant(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @PathVariable String merchantId, HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search merchants by brand id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            getBusinessProfile(userType, authToken, merchantId);
            var categoryMerchant = categoryBrandMerchantService.getBrandsCategoryMapperByMerchantId(merchantId);
            Set<BasicBrandResponseDto> basicBrandResponseDtoSet = new HashSet<>();
            Set<BasicCategoryResponseDto> basicCategoryResponseDtoSet = new HashSet<>();
            BrandsAndCategoriesForMerchantResponseDto brandsAndCategoriesForMerchantResponseDto = null;
            if (categoryMerchant.isPresent()) {
                var categoryList = new ArrayList<>(categoryMerchant.get().getCategories());
                var brandList = new ArrayList<>(categoryMerchant.get().getBrands());
                var categorySet = categoryService.getCategorySetByIds(categoryList);
                categorySet.forEach(category -> basicCategoryResponseDtoSet.add(
                        new BasicCategoryResponseDto(category)));
                var brandSet = brandService.getBrandSetByIdList(brandList);
                brandSet.forEach(brand -> basicBrandResponseDtoSet.add(new BasicBrandResponseDto(brand)));
                brandsAndCategoriesForMerchantResponseDto = new BrandsAndCategoriesForMerchantResponseDto(
                        categoryMerchant.get(), basicCategoryResponseDtoSet, basicBrandResponseDtoSet, timeZone);
            }
            return getSuccessResponse(brandsAndCategoriesForMerchantResponseDto,
                    SuccessResponseStatusType.READ_CATEGORIES_BRANDS_FOR_MERCHANT);
        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {} to read map of categories and brands. userId: {}",
                    merchantId, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning brands and categories was failed for merchantId: {}. userId: {}",
                    merchantId, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get merchant list by categoryId
     *
     * @param userId   userId
     * @param timeZone timeZone
     * @param page     page
     * @param size     size
     * @return merchantListResponseDto
     */
    @GetMapping("/{page}/{size}/category/{categoryId}")
    public ResponseEntity<ResponseWrapper> getMerchantOrBankListByCategoryId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId) {
        return getMerchantOrBankListByCategoryId(userId, timeZone, page, size, categoryId, false);
    }

    /**
     * Get active merchant list by categoryId.
     *
     * @param userId   userId
     * @param timeZone timeZone
     * @param page     page
     * @param size     size
     * @return merchantListResponseDto
     */
    @GetMapping("active/{page}/{size}/category/{categoryId}")
    public ResponseEntity<ResponseWrapper> getActiveMerchantListByCategoryId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId) {
        return getMerchantOrBankListByCategoryId(userId, timeZone, page, size, categoryId, true);
    }

    /**
     * Returns merchant list according to isActiveMerchant status.
     *
     * @param userId             user id
     * @param timeZone           time zone
     * @param page               page
     * @param size               size
     * @param categoryId         category id
     * @param onlyActiveMerchant merchant status True or false
     * @return merchantListResponseDto
     */
    private ResponseEntity<ResponseWrapper> getMerchantOrBankListByCategoryId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            boolean onlyActiveMerchant
    ) {
        try {
            if (!categoryId.equals(ALL)) {
                categoryService.getCategoryById(categoryId);
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            var merchantResponseDtoPage = createMerchantResponseDtoPage(
                    pageable, categoryId, userId, onlyActiveMerchant);
            var merchantListResponseDto = new MerchantListResponseDto(merchantResponseDtoPage);
            log.debug("Successfully return the merchant list for page: {}, size {}, userId {}", page, size, userId);
            return getSuccessResponse(merchantListResponseDto, SuccessResponseStatusType.READ_MERCHANT_LIST);
        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {} to get merchant List by category Id.", userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (InvalidCategoryException e) {
            log.error("Invalid category Id(s) to get merchant List by category Id.", e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (QponCoreException e) {
            log.error("Returning merchant list was failed for page: {}, size {}, userId{}", page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This Endpoint returns List Merchant By Brand ID with searchTerm.
     *
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param brandId    brand id
     * @param searchTerm searchTerm/ALL
     * @return ResponseEntity
     */
    @GetMapping("/{page}/{size}/brand/{brandId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getMerchantListByBrandId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String brandId,
            @PathVariable String searchTerm,
            @PathVariable UserType userType) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search merchants by brand id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!brandId.equals(ALL)) {
                brandService.validateBrandId(brandId);
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            Page<CategoryBrandMerchantIndex> categoryBrandMerchantIndex =
                    categoryBrandMerchantIndexService.getCategoryBrandMerchantIndexPageByBrandId(
                            pageable, brandId, searchTerm, userType);
            var basicMerchantResponseDtoPage =
                    createBasicMerchantResponseDtoPage(userId, categoryBrandMerchantIndex, userType);
            var merchantListResponseDto = new MerchantListResponseDto(basicMerchantResponseDtoPage);
            return getSuccessResponse(merchantListResponseDto, SuccessResponseStatusType.READ_MERCHANT_LIST);
        } catch (InvalidBrandException e) {
            log.error("Invalid brand Id: {} to get merchant List by Brand Id.", brandId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            log.error("Returning merchant list was failed for page: {}, size {}, userId{}, brandId: {}",
                    page, size, userId, brandId, e);
            return getInternalServerError();
        }
    }

    /**
     * This Endpoint returns Merchant List By Category ID with searchTerm.
     *
     * @param userId     userId
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param categoryId categoryID
     * @param searchTerm searchTerm/ All
     * @param userType   MERCHANT/ BANK
     * @return ResponseEntity
     */
    @GetMapping("/{page}/{size}/category/{categoryId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getMerchantOrBankListByCategoryId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable String searchTerm,
            @PathVariable UserType userType) {
        try {
            if (userType == UserType.MERCHANT || userType == UserType.BANK) {
                return getMerchantOrBankListByCategoryId(userId, timeZone, page, size, categoryId, searchTerm,
                        false, userType);
            } else {
                log.error("Invalid userType:{} to get all merchants/ banks by categoryId: {} and searchTerm: {}.",
                        userType.name(), categoryId, searchTerm);
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
        } catch (QponCoreException e) {
            log.error("Returning merchant list was failed for categoryId: {}, page: {}, size {}, userId: {}",
                    categoryId, page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This Endpoint returns Merchant List By Category ID with searchTerm for mobile.
     *
     * @param userId     userId
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param categoryId categoryID
     * @param searchTerm searchTerm/ All
     * @return ResponseEntity
     */
    @GetMapping("active/{page}/{size}/category/{categoryId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getActiveMerchantListByCategoryIdAndSearchTerm(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable UserType userType,
            @PathVariable String categoryId,
            @PathVariable String searchTerm) {
        try {
            if (userType == UserType.MERCHANT || userType == UserType.BANK) {
                return getMerchantOrBankListByCategoryId(userId, timeZone, page, size, categoryId, searchTerm, true, userType);
            } else {
                log.error("Invalid userType:{} to get active merchants/ banks by categoryId: {} and searchTerm: {}.",
                        userType.name(), categoryId, searchTerm);
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_ROLE_TYPE_ACTION);
            }
        } catch (QponCoreException e) {
            log.error("Returning merchant list was failed for categoryId: {}, page: {}, size {}, userId{}",
                    categoryId, page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * @param userId             user id
     * @param timeZone           time zone
     * @param page               page
     * @param size               size
     * @param categoryId         category id
     * @param searchTerm         searchTerm/ All
     * @param onlyActiveMerchant true/false
     * @param userType           MERCHANT/ BANK
     * @return merchantListResponseDto
     */
    private ResponseEntity<ResponseWrapper> getMerchantOrBankListByCategoryId(
            String userId, String timeZone, int page,
            int size, String categoryId,
            String searchTerm, boolean onlyActiveMerchant, UserType userType) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search merchants/banks by category id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!ALL.equals(categoryId)) {
                categoryService.validateCategoryId(categoryId);
            }
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            var categoryBrandMerchantIndexPageByCategoryId =
                    categoryBrandMerchantIndexService.getCategoryBrandMerchantIndex(
                            pageable, categoryId, searchTerm, onlyActiveMerchant, userType);
            var basicMerchantResponseDtoPage =
                    createBasicMerchantResponseDtoPage(userId, categoryBrandMerchantIndexPageByCategoryId, userType);
            var merchantListResponseDto = new MerchantListResponseDto(basicMerchantResponseDtoPage);
            return getSuccessResponse(merchantListResponseDto, SuccessResponseStatusType.READ_MERCHANT_LIST);
        } catch (InvalidCategoryException e) {
            log.error("Invalid category Id: {} to get merchant List by category Id.", categoryId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (QponCoreException e) {
            log.error("Returning merchant list was failed for categoryId: {}, page: {}, size {}, userId{}",
                    categoryId, page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This Endpoint returns Category List By merchant id with searchTerm.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return basicCategoryListResponseDto
     */
    @GetMapping("{merchantId}/category/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getCategoryListByMerchant(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String merchantId,
            @PathVariable String searchTerm, HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search categories by merchant/bank id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            getBusinessProfile(userType, authToken, merchantId);
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            var categoryBrandMerchant = categoryBrandMerchantService.getBrandsCategoryMapperByMerchantId(merchantId);
            var basicCategoryListResponseDto = new BasicCategoryListResponseDto(new PageImpl<>(new ArrayList<>()));
            if (categoryBrandMerchant.isPresent()) {
                var categorySet = categoryBrandMerchant.get().getCategories();
                var categoriesByIds = categoryBrandMerchantService.getCategoriesByIds(pageable, categorySet, searchTerm);
                basicCategoryListResponseDto = new BasicCategoryListResponseDto(categoriesByIds);
            }
            return getSuccessResponse(basicCategoryListResponseDto, SuccessResponseStatusType.READ_CATEGORIES_FOR_MERCHANT);
        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {} to get merchant's category list", merchantId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning merchant's category list was failed for merchantId: {}," + " page: {}, size: {}, userId: {}, time zone: {}", merchantId, page, size, userId, timeZone, e);
            return getInternalServerError();
        }
    }

    /**
     * This Endpoint returns Brand List By merchant id with searchTerm.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return basicBrandListResponseDto
     */
    @GetMapping("{merchantId}/brand/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getBrandListByMerchant(@RequestHeader(name = USER_ID_HEADER) String userId, @RequestHeader(name = TIME_ZONE_HEADER) String timeZone, @PathVariable UserType userType, @Min(DEFAULT_PAGE) @PathVariable int page, @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size, @PathVariable String merchantId, @PathVariable String searchTerm, HttpServletRequest request) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search brands by merchant/bank id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            getBusinessProfile(userType, authToken, merchantId);
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            var categoryBrandMerchant = categoryBrandMerchantService.getBrandsCategoryMapperByMerchantId(merchantId);
            var basicBrandListResponseDto = new BasicBrandListResponseDto(new PageImpl<>(new ArrayList<>()));
            if (categoryBrandMerchant.isPresent()) {
                var brands = categoryBrandMerchant.get().getBrands();
                var brandsByIds = categoryBrandMerchantService.getBrandsByIds(pageable, brands, searchTerm);
                basicBrandListResponseDto = new BasicBrandListResponseDto(brandsByIds);
            }
            return getSuccessResponse(basicBrandListResponseDto, SuccessResponseStatusType.READ_BRAND_FOR_MERCHANT);
        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {} to get merchant's brand list.", merchantId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning merchant's brand list was failed for merchantId: {}," + " page: {}, size: {}, userId: {}, time zone: {}", merchantId, page, size, userId, timeZone, e);
            return getInternalServerError();
        }
    }

    /**
     * Get Bank List By MerchantId.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param merchantId merchant id
     * @param searchTerm search term
     * @param request    request
     * @return Bank List.
     */
    @GetMapping("/{merchantId}/bank/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getBanksListByMerchantId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            HttpServletRequest request) {
        return getBanksListByMerchantId(userId,
                timeZone, page, size, merchantId, searchTerm, request, false);
    }

    /**
     * Get active Bank List By MerchantId.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param merchantId merchant id
     * @param searchTerm search term
     * @param request    request
     * @return Bank List.
     */
    @GetMapping("/active/{merchantId}/bank/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getActiveBanksListByMerchantId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            HttpServletRequest request) {
        return getBanksListByMerchantId(userId,
                timeZone, page, size, merchantId, searchTerm, request, true);
    }

    /**
     * This method return bank list by merchant id, search term and isActiveBank.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param merchantId merchant id
     * @param searchTerm search term
     * @param request    request
     * @return Bank List.
     */
    public ResponseEntity<ResponseWrapper> getBanksListByMerchantId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            HttpServletRequest request,
            boolean onlyActiveBank) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            authUserService.getMerchantBusinessByMerchantId(authToken, merchantId);
            Pageable pageable = PageRequest.of(page, size);
            var bankDealSearchIndex = merchantBankSearchIndexService.getBankMerchantSearchIndexByMerchantIdAndSearchTerm(pageable, merchantId, searchTerm, onlyActiveBank);
            var basicMerchantResponseDtoPage = createBasicMerchantResponseDtoPageForMerchantOrBank(userId, bankDealSearchIndex, UserType.BANK);
            var merchantListResponseDto = new MerchantListResponseDto(basicMerchantResponseDtoPage);
            return getSuccessResponse(merchantListResponseDto, SuccessResponseStatusType.BANK_LIST);
        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {} to get bank list.", merchantId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning bank list was failed for merchant id: {}, page: {}, size: {}, user id: {}.", merchantId, page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get Merchant List By BankId .
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param bankId     bank id
     * @param searchTerm search term
     * @param request    request
     * @return List of merchants
     */
    @GetMapping("/{bankId}/merchant/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getMerchantListByBankId(@RequestHeader(name = USER_ID_HEADER) String userId, @RequestHeader(name = TIME_ZONE_HEADER) String timeZone, @Min(DEFAULT_PAGE) @PathVariable int page, @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size, @PathVariable String bankId, @PathVariable String searchTerm, HttpServletRequest request) {
        return getMerchantListByBankId(userId, timeZone, page, size, bankId, searchTerm, request, false);
    }

    /**
     * Get active Merchant List By Bank Id.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param bankId     bank id
     * @param searchTerm search term
     * @param request    request
     * @return List of merchants
     */
    @GetMapping("/active/{bankId}/merchant/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getActiveMerchantListByBankId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String bankId, @PathVariable String searchTerm,
            @PathVariable("userType") String toUserType,
            HttpServletRequest request) {
        return getMerchantListByBankId(userId,
                timeZone, page, size, bankId, searchTerm, request, true);
    }

    /**
     * Get Merchant List By BankId and isActiveMerchant.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param bankId     bank id
     * @param searchTerm search term
     * @param request    request
     * @return List of merchants
     */
    public ResponseEntity<ResponseWrapper> getMerchantListByBankId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String bankId, @PathVariable String searchTerm,
            HttpServletRequest request,
            boolean onlyActiveMerchant) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            authUserService.getBankBusinessByBankId(authToken, bankId);
            Pageable pageable = PageRequest.of(page, size);
            var bankDealSearchIndexPage = merchantBankSearchIndexService.
                    getBankDealSearchIndexByBankIdAndSearchTerm(pageable, bankId, searchTerm, onlyActiveMerchant);
            var basicMerchantResponseDtoPage =
                    createBasicMerchantResponseDtoPageForMerchantOrBank(userId, bankDealSearchIndexPage,
                            UserType.MERCHANT);
            var merchantListResponseDto = new MerchantListResponseDto(basicMerchantResponseDtoPage);
            return getSuccessResponse(merchantListResponseDto, SuccessResponseStatusType.READ_MERCHANT_LIST);
        } catch (InvalidUserException e) {
            log.error("Invalid bank Id: {} to get merchant list.", bankId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning merchant list failed for bank id: {}, page: {}, size: {}, user id: {}.",
                    bankId, page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method get bulk users from {@link AuthUserService} accordingly user type and create merchant response dto.
     *
     * @param userId                  user id
     * @param bankDealSearchIndexPage bankDealSearchIndex page
     * @param userType                user type
     * @return BasicMerchantBusinessResponseDto page
     */
    private Page<BasicMerchantBusinessResponseDto> createBasicMerchantResponseDtoPageForMerchantOrBank(
            String userId, Page<BankMerchantSearchIndex> bankDealSearchIndexPage, UserType userType) {
        var merchantBankIdList = new ArrayList<String>();
        var merchantResponseDtoList = new ArrayList<BasicMerchantBusinessResponseDto>();
        if (!bankDealSearchIndexPage.isEmpty()) {
            if (UserType.MERCHANT.equals(userType)) {
                for (var bankDealSearchIndex : bankDealSearchIndexPage) {
                    merchantBankIdList.add(bankDealSearchIndex.getMerchantId());
                }
                var bulkUserRequestDto = new BulkUserRequestDto(merchantBankIdList);
                var bulkUserResponseDto =
                        authUserService.getMerchantMap(userId, bulkUserRequestDto, userType);
                merchantResponseDtoList.addAll(bulkUserResponseDto.values());
            } else if (UserType.BANK.equals(userType)) {
                for (var bankDealSearchIndex : bankDealSearchIndexPage) {
                    merchantBankIdList.add(bankDealSearchIndex.getBankId());
                }
                var bulkUserRequestDto = new BulkUserRequestDto(merchantBankIdList);
                var bulkUserResponseDto =
                        authUserService.getMerchantMap(userId, bulkUserRequestDto, userType);
                merchantResponseDtoList.addAll(bulkUserResponseDto.values());
            } else {
                throw new UnsupportedUserTypeException("This user type: " + userType + "not allowed to do this action");
            }
        }
        return new PageImpl<>(merchantResponseDtoList);
    }

    /**
     * This method get bulk users from {@link AuthUserService} and create merchant response dto.
     *
     * @param categoryId categoryId
     * @param userId     userId
     * @return Page<BasicMerchantBusinessResponseDto>
     */
    private Page<BasicMerchantBusinessResponseDto> createMerchantResponseDtoPage(
            Pageable pageable, String categoryId, String userId, boolean onlyActiveMerchant) {
        var merchantIdList = categoryBrandMerchantService.
                findAllMerchantsByCategoryId(pageable, categoryId, onlyActiveMerchant);
        var merchantResponseDtoList = new ArrayList<BasicMerchantBusinessResponseDto>();
        if (!merchantIdList.isEmpty()) {
            var bulkUserRequestDto = new BulkUserRequestDto(merchantIdList);
            var bulkUserResponseDto =
                    authUserService.getMerchantMap(userId, bulkUserRequestDto, UserType.MERCHANT);
            merchantResponseDtoList.addAll(bulkUserResponseDto.values());
        }
        return new PageImpl<>(merchantResponseDtoList);
    }

    /**
     * This method get bulk users from {@link AuthUserService} and create merchant response dto for given
     * CategoryBrandMerchantIndex List
     *
     * @param userId                       user id
     * @param categoryBrandMerchantIndexes categoryBrandMerchantIndex List
     * @param userType                     MERCHANT/BANK
     * @return BasicMerchantResponseDto Page
     */
    private Page<BasicMerchantBusinessResponseDto> createBasicMerchantResponseDtoPage(
            String userId, Page<CategoryBrandMerchantIndex> categoryBrandMerchantIndexes, UserType userType) {
        var merchantIdList = new ArrayList<String>();
        var merchantResponseDtoList = new ArrayList<BasicMerchantBusinessResponseDto>();
        if (!categoryBrandMerchantIndexes.isEmpty()) {
            for (var categoryBrandMerchantIndex : categoryBrandMerchantIndexes) {
                merchantIdList.add(categoryBrandMerchantIndex.getMerchantId());
            }
            var bulkUserRequestDto = new BulkUserRequestDto(merchantIdList);
            var bulkUserResponseDto = authUserService.getMerchantMap(
                    userId, bulkUserRequestDto, userType);
            merchantResponseDtoList.addAll(bulkUserResponseDto.values());
        }
        return new PageImpl<>(merchantResponseDtoList);
    }
}
