package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.response.DealListResponseDto;
import com.swivel.cc.base.domain.response.DealsOfTheDayHistoryGroupResponseDto;
import com.swivel.cc.base.domain.response.DealsOfTheDayHistoryResponseDto;
import com.swivel.cc.base.enums.DealsOfTheDayHistoryFilter;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidCategoryException;
import com.swivel.cc.base.exception.InvalidDateOptionException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.service.AuthUserService;
import com.swivel.cc.base.service.CategoryService;
import com.swivel.cc.base.service.DealsOfTheDayService;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.List;

/**
 * Deals of the day controller
 */
@Validated
@Slf4j
@RestController
@RequestMapping("api/v1/deals/deals-of-the-day")
public class DealsOfTheDayController extends Controller {

    private static final int DOD_PAGE_MAX_SIZE = 10;
    private static final int MAX_PAGE_SIZE_FOR_HISTORY = 6;
    private static final int HISTORY_PAGE_SIZE = 1;
    private static final String LOG_MESSAGE_DETAILS = "{} page: {}, size {}, categoryId: {}, merchantId: {}, " +
            "searchTerm: {}, userType: {}, for userId: {}";
    private final DealsOfTheDayService dealsOfTheDayService;
    private final CategoryService categoryService;
    private final AuthUserService authUserService;

    @Autowired
    public DealsOfTheDayController(Translator translator, DealsOfTheDayService dealsOfTheDayService,
                                   CategoryService categoryService, AuthUserService authUserService) {
        super(translator);
        this.dealsOfTheDayService = dealsOfTheDayService;
        this.categoryService = categoryService;
        this.authUserService = authUserService;
    }

    /**
     * This method return deals of the days by category Id, merchant Id, search term
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
    @GetMapping("/{page}/{size}/category/{categoryId}/{userType}/{merchantId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getDealsOfTheDay(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String categoryId,
            @PathVariable String merchantId,
            @PathVariable UserType userType,
            @PathVariable String searchTerm) {
        try {
            if (userType!=UserType.MERCHANT && userType!=UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "search deal-of-the-day");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            if (!categoryId.equals(ALL)) {
                categoryService.validateCategoryId(categoryId);
            }
            Pageable pageable = PageRequest.of(page, size);
            var dealsOfTheDayPage = dealsOfTheDayService.
                    dealsOfTheDayPageForMerchant(pageable, categoryId, merchantId, searchTerm, userType);
            var bulkUserResponseMap =
                    authUserService.getMerchantMapForDeals(dealsOfTheDayPage, userId, userType);
            var dealListResponseDto = new DealListResponseDto(dealsOfTheDayPage, bulkUserResponseMap, timeZone);
            log.debug(LOG_MESSAGE_DETAILS, "Successfully returned deals of the day.", page, size, categoryId,
                    merchantId, searchTerm, userType, userId);
            return getSuccessResponse(dealListResponseDto, SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (InvalidCategoryException e) {
            log.error(LOG_MESSAGE_DETAILS, "Invalid category Id for related categories.", page, size, categoryId,
                    merchantId, searchTerm, userType, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CATEGORY_ID);
        } catch (QponCoreException e) {
            log.error(LOG_MESSAGE_DETAILS, "Returning deals of the days was failed.", page, size, categoryId,
                    merchantId, searchTerm, userType, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method is used to get deals of the day history.
     *
     * @param userId     userId
     * @param timeZone   timeZone
     * @param page       page
     * @param size       size
     * @param searchTerm ALL/searchTerm
     * @param option     filter option
     * @param request    http request
     * @return deals of the day history.
     */
    @GetMapping("/{userType}/{page}/{size}/search/{searchTerm}/filter/{option}")
    public ResponseEntity<ResponseWrapper> getDealsOfTheDayHistory(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable UserType userType,
            @Min(DEFAULT_PAGE) @Max(MAX_PAGE_SIZE_FOR_HISTORY) @PathVariable int page,
            @Positive @Max(DOD_PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String searchTerm,
            @PathVariable String option,
            HttpServletRequest request

    ) {
        try {
            if (userType!=UserType.MERCHANT && userType!=UserType.BANK) {
                log.debug(UNSUPPORTED_USER_TYPE_ACTION_LOG, userType, "get deals-of-the-day history");
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_USER_TYPE_ACTION);
            }
            DealsOfTheDayHistoryFilter filter = DealsOfTheDayHistoryFilter.getOption(option);
            List<DealsOfTheDayHistoryResponseDto> historyResponseList =
                    dealsOfTheDayService.getDealsOfTheDayHistory(timeZone, searchTerm, filter, userId, userType, page);
            Pageable pageable = PageRequest.of(page, HISTORY_PAGE_SIZE);
            Page<DealsOfTheDayHistoryResponseDto> historyResponsePage =
                    new PageImpl<>(historyResponseList, pageable, historyResponseList.size());
            return getSuccessResponse(
                    new DealsOfTheDayHistoryGroupResponseDto(historyResponsePage, historyResponseList.get(page)),
                    SuccessResponseStatusType.READ_DEAL_LIST);
        } catch (InvalidDateOptionException e) {
            log.error("Invalid filter option: {} for userId: {}", option, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DATE_OPTION);
        } catch (QponCoreException e) {
            log.error("Returning deals of the day history was failed for userId: {}. " +
                    "Page: {}, size: {}, searchTerm: {}, filter: {}", userId, page, size, searchTerm, option, e);
            return getInternalServerError();
        }
    }
}
