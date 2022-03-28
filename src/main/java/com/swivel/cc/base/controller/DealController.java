package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.request.DealApprovalStatusUpdateRequestDto;
import com.swivel.cc.base.domain.request.DealRequestDto;
import com.swivel.cc.base.domain.request.DealUpdateRequestDto;
import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.domain.response.DealListResponseDto;
import com.swivel.cc.base.domain.response.DealResponseDto;
import com.swivel.cc.base.enums.*;
import com.swivel.cc.base.exception.*;
import com.swivel.cc.base.service.*;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.time.LocalDate;
import java.util.*;

/**
 * Deal Controller
 */
@CrossOrigin
@Slf4j
@RestController
@Validated
@RequestMapping("api/v1/deals")
public class DealController extends Controller {

    private static final String LOG_MESSAGE = "page: {}, size {}, categoryId: {}, merchantId: {}, searchTerm: {} for userId: {}";
    private final DealService dealService;
    private final BrandService brandService;
    private final CategoryService categoryService;
    private final AuthUserService authUserService;
    private final DealCodeService dealCodeService;

    @Autowired
    public DealController(DealService dealService, BrandService brandService, CategoryService categoryService,
                          AuthUserService authUserService, Translator translator, DealCodeService dealCodeService) {
        super(translator);
        this.dealService = dealService;
        this.brandService = brandService;
        this.categoryService = categoryService;
        this.authUserService = authUserService;
        this.dealCodeService = dealCodeService;
    }

    /**
     * Create Deal
     *
     * @param userId         userId
     * @param userType       MERCHANT/ BANK
     * @param dealRequestDto dealRequestDto
     * @param timeZone       time zone
     * @return SuccessResponse / ErrorResponse
     */
    @Secured({MERCHANT_ROLE})
    @PostMapping("/{userType}")
    public ResponseEntity<ResponseWrapper> createDeal(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @RequestBody DealRequestDto dealRequestDto,
            HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "create deal");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!dealRequestDto.isRequiredAvailable(userType)) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            if (!dealRequestDto.isValidStartDate()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_VALID_FROM_DATE);
            }
            if (!dealRequestDto.isValidPrice()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_PRICE);
            }
            if (!dealRequestDto.isValidExpireDate()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_EXPIRED_ON_DATE);
            }
            if (!dealRequestDto.isValidDeductionValue()) {
                var error = (dealRequestDto.getDeductionType() == DeductionType.AMOUNT) ?
                        ErrorResponseStatusType.INVALID_DEDUCTION_AMOUNT :
                        ErrorResponseStatusType.INVALID_DEDUCTION_PERCENTAGE;
                return getErrorResponse(error);
            }
            var businessProfile =
                    getBusinessProfile(userType, authToken, dealRequestDto.getMerchantId());
            if (!businessProfile.isActive()) {
                log.debug("Error Creating deal for Inactive merchant for merchant id: {}.",
                        businessProfile.getMerchantId());
                return getErrorResponse(ErrorResponseStatusType.INACTIVE_MERCHANT_FOR_CREATE_DEAL);
            }
            BusinessMerchantResponseDto merchant = null;
            if (UserType.BANK.equals(userType)) {
                merchant = authUserService.getMerchantBusinessByMerchantId(authToken, dealRequestDto.getShopId());
            }
            String dealCode = dealCodeService.generateAndSave(LocalDate.now(), userType);
            var dealCreateResponseDto = readAndCreateResponse(dealRequestDto, timeZone,
                    businessProfile, userType, merchant, dealCode);
            String objectToJson = dealCreateResponseDto.toLogJson();
            log.debug("Successfully created deal for userId: {}, to userType: {}, deal response {}",
                    userId, userType, objectToJson);
            return getSuccessResponse(dealCreateResponseDto,
                    SuccessResponseStatusType.CREATE_DEAL);
        } catch (InvalidUserException e) {
            log.error("Invalid/ Inactive merchant id :{} for userType :{}. user id :{}",
                    dealRequestDto.getMerchantId(), userType, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INACTIVE_MERCHANT_FOR_CREATE_DEAL);
        } catch (InvalidCategoryException e) {
            log.error("Reading related category id for user: {} was failed.", userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (InvalidBrandException e) {
            log.error("Error when reading related brand id for user: {}.", userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            log.error("Creating new deal for userId: {} was failed. userType: {}", userId, userType, e);
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

    @PutMapping("/{userType}")
    public ResponseEntity<ResponseWrapper> updateDeal(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @RequestBody DealUpdateRequestDto dealUpdateRequestDto,
            HttpServletRequest request) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "update deal");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!dealUpdateRequestDto.isRequiredAvailable(userType)) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            if (!dealUpdateRequestDto.isValidStartDate()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_VALID_FROM_DATE);
            }
            if (!dealUpdateRequestDto.isValidPrice()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_PRICE);
            }
            if (!dealUpdateRequestDto.isValidExpireDate()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_EXPIRED_ON_DATE);
            }
            if (!dealUpdateRequestDto.isValidDeductionValue()) {
                var error = (dealUpdateRequestDto.getDeductionType() == DeductionType.AMOUNT) ?
                        ErrorResponseStatusType.INVALID_DEDUCTION_AMOUNT :
                        ErrorResponseStatusType.INVALID_DEDUCTION_PERCENTAGE;
                return getErrorResponse(error);
            }
            Deal deal = dealService.getDeal(dealUpdateRequestDto.getId(), userType);
            if (!deal.getApprovalStatus().equals(ApprovalStatus.PENDING)) {
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_DEAL_UPDATE);
            }
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            var businessProfile =
                    getBusinessProfile(userType, authToken, dealUpdateRequestDto.getMerchantId());
            var updateDeal =
                    dealService.updateDeal(dealUpdateRequestDto, userType, deal, businessProfile);
            var dealCreateResponseDto = new DealResponseDto(updateDeal, timeZone,
                    new BasicMerchantBusinessResponseDto(businessProfile));
            String objectToJson = dealCreateResponseDto.toLogJson();
            log.debug("Successfully updated deal for userId: {}, to userType: {}, deal response {}",
                    userId, userType, objectToJson);
            return getSuccessResponse(dealCreateResponseDto, SuccessResponseStatusType.UPDATE_DEAL);
        } catch (InvalidDealException e) {
            log.error("Error when reading deal, dealId {}", dealUpdateRequestDto.getId(), e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DEAL_ID);
        } catch (InvalidUserException e) {
            log.error("Reading merchant id for user: {}, to userType: {} was failed.", userId, userType, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (InvalidCategoryException e) {
            log.error("Getting related category(s) was failed for dealId: {}, userId: {}.",
                    dealUpdateRequestDto.getId(), userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (InvalidBrandException e) {
            log.error("Get related brand(s) was failed for dealId: {}, userId: {}.",
                    dealUpdateRequestDto.getId(), userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            log.error("Updating existing deal was failed. dealId: {}, userId: {}, to userType: {} ",
                    dealUpdateRequestDto.getId(), userId, userType, e);
            return getInternalServerError();
        }
    }

    /**
     * Get paginated basic deal list
     *
     * @return Deal List Response
     */
    @Secured({ADMIN_ROLE})
    @GetMapping("/{page}/{size}/search/ALL")
    public ResponseEntity<ResponseWrapper> getDealList(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size
    ) {
        try {
            Page<Deal> dealPage = dealService
                    .listAllDeals(generateAPageable(page, size, DEFAULT_NATIVE_SORT, false));
            var dealListResponseDto = createDealListResponseDto(dealPage, userId, timeZone);
            log.debug("Successfully returned all deal list for page: {}, size {} for userId: {}",
                    page, size, userId);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (QponCoreException e) {
            log.error("Returning all deal list was failed for page: {}, size {} for userId: {}",
                    page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get paginated basic deal list
     *
     * @return Deal List Response
     */
    @Secured({ADMIN_ROLE})
    @GetMapping("/{page}/{size}/{userType}/{merchantId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getDealList(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable UserType userType,
            @PathVariable String merchantId,
            @PathVariable String searchTerm
    ) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search deal by merchant/bank id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            var dealPage =
                    dealService.searchDeals(generateAPageable(page, size, DEFAULT_SORT, false),
                            merchantId, searchTerm, userType);
            var dealListResponseDto = new DealListResponseDto(dealPage, timeZone);
            log.debug("Successfully returned search deal list for page: {}, size: {} for userId: {}, userType: {}",
                    page, size, userId, userType);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (QponCoreException e) {
            log.error("Returning search deal list was failed for page: {}, size {} for userId: {}, userType: {}",
                    page, size, userId, userType, e);
            return getInternalServerError();
        }
    }

    /**
     * Get paginated PENDING deal list
     *
     * @param userId     user Id
     * @param timeZone   timeZone
     * @param userType   BANK/MERCHANT
     * @param page       page number
     * @param size       page size
     * @param searchTerm search term
     * @return pending deal list
     */
    @Secured({ADMIN_ROLE})
    @GetMapping("/{userType}/PENDING/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getPendingDealList(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String searchTerm
    ) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search pending deal");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            Pageable pageable = searchTerm.equals(ALL) ?
                    generateAPageable(page, size, DEFAULT_SORT, false)
                    : generateAPageable(page, size, DEFAULT_NATIVE_SORT, false);
            var dealPage = dealService.getPendingDeals(pageable, searchTerm, userType);
            var dealListResponseDto = new DealListResponseDto(dealPage, timeZone);
            log.debug("Successfully returned pending deal list for page: {}, size: {}, userType: {}, userId: {}",
                    page, size, userType, userId);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (QponCoreException e) {
            log.error("Returning pending deal list was failed for page: {}, size: {}, userType: {}, userId: {}",
                    page, size, userType, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get paginated deal list for merchantId
     *
     * @return Deal List Response
     */
    @GetMapping("/{page}/{size}/merchant/{merchantId}")
    public ResponseEntity<ResponseWrapper> getDealListForMerchant(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String merchantId,
            HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            var businessMerchant =
                    authUserService.getMerchantBusinessByMerchantId(authToken, merchantId);
            var dealPage = dealService
                    .listAllDealsByMerchant(generateAPageable(page, size, DEFAULT_NATIVE_SORT, false), merchantId);
            var dealListResponseDto =
                    new DealListResponseDto(dealPage, new BasicMerchantBusinessResponseDto(businessMerchant), timeZone);
            log.debug("Successfully returned the deal list for merchant Id for page: {}, size {} for userId: {}",
                    page, size, userId);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);

        } catch (InvalidUserException e) {
            log.error("Invalid merchant Id: {}.", userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning deal list for merchant Id was failed for page: {}, size {} for userId: {}",
                    page, size, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Soft delete of a deal
     *
     * @param userId   userId
     * @param timeZone timeZone
     * @param userType Merchant/Bank
     * @param dealId   dealId
     * @return SuccessResponse / ErrorResponse
     */
    @Secured({MERCHANT_ROLE})
    @DeleteMapping("/{userType}/{dealId}")
    public ResponseEntity<ResponseWrapper> deleteDeal(@RequestHeader(name = USER_ID_HEADER) String userId,
                                                      @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
                                                      @PathVariable UserType userType,
                                                      @PathVariable String dealId) {

        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "get deal by id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            dealService.deleteDeal(dealId, userType);
            log.debug("Successfully deal deleted by userId {}, dealId {}, userType {}", userId, dealId, userType);
            return getSuccessResponse(null, SuccessResponseStatusType.DELETE_DEAL);

        } catch (InvalidDealException e) {
            log.error("Error when delete deal, dealId {}", dealId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DEAL_ID);
        } catch (QponCoreException e) {
            log.error("Deleting deal. dealId {}, userType {} was failed.", dealId, userType, e);
            return getInternalServerError();
        }
    }

    /**
     * Approve a deal
     *
     * @param userId                             userId
     * @param timeZone                           timeZone
     * @param userType                           BANK/MERCHANT
     * @param dealApprovalStatusUpdateRequestDto dealStatusUpdateRequestDto
     * @return SuccessResponse / ErrorResponse
     */
    @Secured({ADMIN_ROLE})
    @PutMapping("/{userType}/approve")
    public ResponseEntity<ResponseWrapper> approveDeal(@RequestHeader(name = USER_ID_HEADER) String userId,
                                                       @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
                                                       @PathVariable UserType userType,
                                                       @RequestBody DealApprovalStatusUpdateRequestDto
                                                               dealApprovalStatusUpdateRequestDto,
                                                       HttpServletRequest request) {

        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "approve deal");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!dealApprovalStatusUpdateRequestDto.isRequiredAvailable()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            dealService.updateApprovalOfDeal(dealApprovalStatusUpdateRequestDto, authToken, userType);
            log.debug("Successfully approved deal by userId {}, userType{}, dealId {}",
                    userId, userType, dealApprovalStatusUpdateRequestDto.getId());
            return getSuccessResponse(null, SuccessResponseStatusType.APPROVE_DEAL);

        } catch (InvalidUserException e) {
            log.error("Reading merchant id for user: {}, userType: {} was failed.", userId, userType, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (InvalidDealException e) {
            log.error("Reading the deal by dealId: {} was failed for: {}",
                    dealApprovalStatusUpdateRequestDto.getId(), dealApprovalStatusUpdateRequestDto.getApprovalStatus(), e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DEAL_ID);
        } catch (QponCoreException e) {
            log.error("Approve/Reject deal was failed. dealId {}, userType {}, userId {}.",
                    dealApprovalStatusUpdateRequestDto.getId(), userType, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Read deal by Id
     *
     * @param userId   user Id
     * @param timeZone timeZone
     * @param dealId   request deal Id
     * @return deal response Dto
     */
    @GetMapping("/{userType}/{dealId}")
    public ResponseEntity<ResponseWrapper> getDetailDeal(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @PathVariable String dealId,
            HttpServletRequest request) {

        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "get deal detail");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            var deal = dealService.getDeal(dealId, userType);
            var businessProfile =
                    getBusinessProfile(userType, authToken, deal.getMerchantId());
            var dealOwnerResponseDto = new BasicMerchantBusinessResponseDto(businessProfile);
            DealResponseDto detailDealResponseDto;
            if (userType.equals(UserType.MERCHANT)) {
                detailDealResponseDto = new DealResponseDto(deal, timeZone, dealOwnerResponseDto);
            } else {
                var storeDetailsForBankDeal =
                        getBusinessProfile(UserType.MERCHANT, authToken, deal.getShopId());
                var storeResponseDto = new BasicMerchantBusinessResponseDto(storeDetailsForBankDeal);
                detailDealResponseDto = new DealResponseDto(deal, timeZone, storeResponseDto, dealOwnerResponseDto);
            }
            detailDealResponseDto.setDealSource(userType.toString());
            return getSuccessResponse(detailDealResponseDto, SuccessResponseStatusType.READ_DEAL);
        } catch (InvalidDealException e) {
            log.error("Invalid dealId: {} for user Id: {}", dealId, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DEAL_ID);
        } catch (QponCoreException e) {
            log.error("Getting deal detail. dealId: {}, userId: {}, userType: {} was failed.",
                    dealId, userId, userType, e);
            return getInternalServerError();
        }
    }

    /**
     * This method return deal list by category Id, merchant Id, search term
     *
     * @param userId     userId
     * @param timeZone   timeZone
     * @param page       page
     * @param size       size
     * @param categoryId categoryId / ALL
     * @param merchantId merchantId / ALL
     * @param searchTerm searchTerm / ALL
     * @return ResponseEntity Paginated deals list
     */
    @GetMapping("/{page}/{size}/category/{categoryId}/{userType}/{merchantId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> searchDeals(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            @PathVariable UserType userType
    ) {
        return searchDeals(userId, timeZone,
                generateAPageable(page, size, DEFAULT_NATIVE_SORT, false),
                categoryId, merchantId, searchTerm, false, false, userType);
    }

    /**
     * This method return active deal list for active merchant by category Id, merchant Id, search term.
     *
     * @param userId     userId
     * @param timeZone   timeZone
     * @param page       page
     * @param size       size
     * @param categoryId categoryId
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @return Paginated deals list
     */
    @GetMapping("active/{page}/{size}/category/{categoryId}/{userType}/active/{merchantId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> searchActiveDealsForActiveMerchant(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            @PathVariable UserType userType) {
        return searchDeals(userId, timeZone,
                generateAPageable(page, size, DEFAULT_SORT, false),
                categoryId, merchantId, searchTerm, true, true, userType);
    }

    /**
     * Returns Deals according to DealStatus and ActiveMerchant Status.
     *
     * @param userId             user id
     * @param timeZone           time zone
     * @param pageable           pageable
     * @param categoryId         category id
     * @param merchantId         merchant id
     * @param searchTerm         search term
     * @param onlyActiveDeals    true/ false
     * @param onlyActiveMerchant true/ false
     * @return Deal List
     */
    private ResponseEntity<ResponseWrapper> searchDeals(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            Pageable pageable,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            boolean onlyActiveDeals,
            boolean onlyActiveMerchant,
            UserType userType
    ) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search deals");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!categoryId.equals(ALL)) {
                categoryService.validateCategoryId(categoryId);
            }
            var dealPage = dealService.getAllSearchDeals(pageable, categoryId, merchantId, ALL,
                    searchTerm, onlyActiveDeals, onlyActiveMerchant, userType);
            var dealListResponseDto = new DealListResponseDto(dealPage, timeZone);
            log.debug("Successfully returned deal list for mobile users for " + LOG_MESSAGE, pageable.getPageNumber(),
                    pageable.getPageSize(), categoryId, merchantId, searchTerm, userId);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (InvalidCategoryException e) {
            log.error("Invalid category Id for related categories. " + LOG_MESSAGE, pageable.getPageNumber(),
                    pageable.getPageSize(), categoryId, merchantId, searchTerm, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (QponCoreException e) {
            log.error("Returning deal list for mobile users was failed for " + LOG_MESSAGE, pageable.getPageNumber(),
                    pageable.getPageSize(), categoryId, merchantId, searchTerm, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method return recently expire deals by category Id, merchant Id, search term
     *
     * @param userId     userId
     * @param timeZone   timeZone
     * @param page       page
     * @param size       size
     * @param categoryId categoryId
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @return ResponseEntity Paginated deals list
     */
    @GetMapping("/recently-expire/{page}/{size}/category/{categoryId}/{userType}/{merchantId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getRecentlyExpireDeals(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable UserType userType,
            @PathVariable String searchTerm) {
        return getRecentlyExpireDeals(
                userId, timeZone, generateAPageable(page, size, EXPIRED_ON_SORT, true),
                categoryId, merchantId, searchTerm, false, false, userType);
    }

    /**
     * This method return recently expire deals by category Id, merchant Id, search term for mobile
     *
     * @param userId     userId
     * @param timeZone   timeZone
     * @param page       page
     * @param size       size
     * @param categoryId categoryId
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @return deal List
     */
    @GetMapping("/recently-expire/{page}/{size}/category/{categoryId}/{userType}/active/{merchantId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getRecentlyExpireDealsForMobile(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable UserType userType,
            @PathVariable String searchTerm) {
        return getRecentlyExpireDeals(
                userId, timeZone, generateAPageable(page, size, EXPIRED_ON_SORT, true),
                categoryId, merchantId, searchTerm, true, true, userType);
    }

    /**
     * This method return recently expire deals by category Id, merchant Id, search term  and merchant active status.
     *
     * @param userId             user id
     * @param timeZone           time zone
     * @param pageable           pageable
     * @param categoryId         category id
     * @param merchantId         merchant id
     * @param searchTerm         search term
     * @param onlyActiveMerchant merchant active status true/ false
     * @return Deal List
     */
    private ResponseEntity<ResponseWrapper> getRecentlyExpireDeals(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            Pageable pageable,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable String searchTerm,
            boolean onlyActiveDeal,
            boolean onlyActiveMerchant,
            UserType userType
    ) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "get recently expired deal");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!categoryId.equals(ALL)) {
                categoryService.validateCategoryId(categoryId);
            }
            var dealPage = dealService.getRecentlyExpireDeals(pageable, categoryId, merchantId, ALL,
                    searchTerm, userType);
            var dealListResponseDto = new DealListResponseDto(dealPage, timeZone);
            log.debug("Successfully returned  recently expire deals  for " +
                            LOG_MESSAGE, pageable.getPageNumber(),
                    pageable.getPageSize(), categoryId, merchantId, searchTerm, userId);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (InvalidCategoryException e) {
            log.error("Invalid category Id for related categories. " + LOG_MESSAGE, pageable.getPageNumber(),
                    pageable.getPageSize(), categoryId, merchantId, searchTerm, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (QponCoreException e) {
            log.error("Returning recently expire deals was failed. " + LOG_MESSAGE, pageable.getPageNumber(),
                    pageable.getPageSize(), categoryId, merchantId, searchTerm, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method return deal list  by category id & search term
     *
     * @param userId     userId
     * @param page       page
     * @param size       size
     * @param categoryId categoryId
     * @param searchTerm searchTerm / ALL
     * @return ResponseEntity Paginated deals list
     */
    @GetMapping("/{userType}/category/{categoryId}/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getDealsByCategoryId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable UserType userType,
            @PathVariable String searchTerm) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search deals by category id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            categoryService.validateCategoryId(categoryId);
            var dealPage =
                    dealService.getAllSearchDeals(
                            generateAPageable(page, size, DEFAULT_NATIVE_SORT, false),
                            categoryId, ALL, ALL, searchTerm, false, false, userType);
            var dealListResponseDto = new DealListResponseDto(dealPage, timeZone);
            log.debug("Successfully return the searchDeal list for page: {}, size: {}, userType: {}, userId:{}, " +
                    "categoryId: {} and searchTerm: {}", page, size, userType, userId, categoryId, searchTerm);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);

        } catch (InvalidCategoryException e) {
            log.error("Invalid category Id: {} for get deals by category id for page: {}, size: {}, userType: {}," +
                    " userId: {}, searchTerm: {}.", categoryId, page, size, userType, userId, searchTerm, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (QponCoreException e) {
            log.error("Returning deal list by Category Id was failed for page: {}, size: {}, userType: {}, " +
                            "userId:{}, categoryId: {} and searchTerm: {}", page, size, userType, userId, categoryId,
                    searchTerm, e);
            return getInternalServerError();
        }
    }

    /**
     * This method return deal list  by brand id & search term
     *
     * @param userId     userId
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param brandId    brand id
     * @param searchTerm search term
     * @return ResponseEntity Paginated deals list
     */
    @GetMapping("/{userType}/brand/{brandId}/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getDealsByBrandId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String brandId,
            @PathVariable UserType userType,
            @PathVariable String searchTerm) {
        try {
            if (userType != UserType.MERCHANT && userType != UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search deals by brand id");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            brandService.validateBrandId(brandId);
            var dealPage = dealService.getAllSearchDeals(
                    generateAPageable(page, size, DEFAULT_NATIVE_SORT, false),
                    ALL, ALL, brandId, searchTerm, false, false, userType);
            var dealListResponseDto = new DealListResponseDto(dealPage, timeZone);
            log.debug("Successfully return the Deal search list for page: {}, size: {}, userType: {}, userId:{}, " +
                    "brandId: {} and searchTerm: {}", page, size, userType, userId, brandId, searchTerm);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);

        } catch (InvalidBrandException e) {
            log.error("Invalid brand Id: {} for get deals by brand id for page: {}, size: {}, userType: {}, " +
                    "userId: {}, searchTerm: {}.", brandId, page, size, userType, userId, searchTerm, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            log.error("Returning deal list by brand Id was failed for page: {}, size {}, brandId: {}, " +
                    "userType: {}, userId: {} and searchTerm: {}", page, size, brandId, userType, userId, searchTerm, e);
            return getInternalServerError();
        }
    }

    /**
     * This method is used to get active deals list for bankId & merchantId.
     *
     * @param userId     userId
     * @param timeZone   timeZone
     * @param page       page
     * @param size       size
     * @param bankId     bankId
     * @param merchantId merchantId
     * @return active deals list.
     */
    @GetMapping("/active/{page}/{size}/bank/{bankId}/merchant/{merchantId}")
    public ResponseEntity<ResponseWrapper> getActiveDealsByBankIdAndMerchantId(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String bankId,
            @PathVariable String merchantId,
            HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            var merchantProfile = getBusinessProfile(UserType.MERCHANT, authToken, merchantId);
            var bankProfile = getBusinessProfile(UserType.BANK, authToken, bankId);
            if (!merchantProfile.isActive()) {
                return getErrorResponse(ErrorResponseStatusType.INACTIVE_MERCHANT);
            }
            Pageable pageable = generateAPageable(page, size, DEFAULT_SORT, false);
            Page<BankDeal> dealPage =
                    dealService.getActiveDealsForActiveBankAndMerchant(pageable, bankId, merchantId);
            var merchantBusinessResponseDto = new BasicMerchantBusinessResponseDto(merchantProfile);
            var bankBusinessResponseDto = new BasicMerchantBusinessResponseDto(bankProfile);
            var dealListResponseDto = new DealListResponseDto(merchantBusinessResponseDto, bankBusinessResponseDto,
                    dealPage, timeZone);
            log.debug("Successfully returned active deals list for bankId: {} and for merchantId: {}. " +
                    "page: {}, size: {}", bankId, merchantId, page, size);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (InvalidUserException e) {
            log.debug("Invalid merchantId: {}", merchantId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_MERCHANT_ID);
        } catch (QponCoreException e) {
            log.error("Returning active deals for active bank & merchant was failed. " +
                            "userId: {}, bankId: {}, merchantId: {}, page: {}, size: {}.",
                    userId, bankId, merchantId, page, size, e);
            return getInternalServerError();
        }
    }

    /**
     * This method creates deal response dto
     *
     * @param dealPage dealPage
     * @param userId   userId
     * @return DealListResponseDto
     */
    private DealListResponseDto createDealListResponseDto(Page<Deal> dealPage, String userId,
                                                          String timeZone) {
        List<String> userIds = new ArrayList<>();
        dealPage.getContent().forEach(deal -> userIds.add(deal.getMerchantId()));
        if (!dealPage.getContent().isEmpty()) {
            var bulkUserRequestDto = new BulkUserRequestDto(userIds);
            var bulkUserResponseMap = authUserService.
                    getMerchantMap(userId, bulkUserRequestDto, UserType.MERCHANT);
            return new DealListResponseDto(dealPage, bulkUserResponseMap, timeZone);
        }
        return new DealListResponseDto(dealPage, new HashMap<>(), timeZone);
    }

    /**
     * This method create deal response dto
     *
     * @param dealRequestDto dealRequestDto
     * @param timeZone       time zone
     * @return Deal response Dto
     */
    private DealResponseDto readAndCreateResponse(DealRequestDto dealRequestDto, String timeZone,
                                                  BusinessMerchantResponseDto businessMerchantResponseDto,
                                                  UserType userType, BusinessMerchantResponseDto merchant, String dealCode) {
        if (!userType.toString().equalsIgnoreCase(businessMerchantResponseDto.getProfileType()))
            throw new InvalidUserException("User type doesn't match with user id.");
        Set<Brand> brands = new HashSet<>();
        if (dealRequestDto.isBrandIdsAvailable()) {
            brands.addAll(brandService.getBrandSetByIdList(dealRequestDto.getBrandIds()));
            var categories = categoryService.getCategorySetByIds(dealRequestDto.getCategoryIds());
            var merchantResponseDto = new BasicMerchantBusinessResponseDto(businessMerchantResponseDto);
            var deal = new Deal(dealRequestDto, brands, categories, dealCode);
            dealService.createDeal(deal, merchantResponseDto, userType, dealRequestDto, merchant);
            return new DealResponseDto(deal, timeZone, merchantResponseDto);
        } else {
            var categories = categoryService.getCategorySetByIds(dealRequestDto.getCategoryIds());
            var merchantResponseDto = new BasicMerchantBusinessResponseDto(businessMerchantResponseDto);
            var deal = new Deal(dealRequestDto, brands, categories, dealCode);
            dealService.createDeal(deal, merchantResponseDto, userType, dealRequestDto, merchant);
            return new DealResponseDto(deal, timeZone, merchantResponseDto);
        }
    }

    /**
     * @param page            page
     * @param size            size
     * @param sortBy          sortBy
     * @param isAscendingTrue true/ false
     * @return pageable
     */
    private Pageable generateAPageable(int page, int size, String sortBy, boolean isAscendingTrue) {
        return (isAscendingTrue) ? PageRequest.of(page, size, Sort.by(sortBy).ascending())
                : PageRequest.of(page, size, Sort.by(sortBy).descending());
    }
}
