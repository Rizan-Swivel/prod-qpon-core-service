package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.request.ReportRequestDto;
import com.swivel.cc.base.domain.response.*;
import com.swivel.cc.base.enums.*;
import com.swivel.cc.base.exception.InvalidDateOptionException;
import com.swivel.cc.base.exception.InvalidDealException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.service.AuthUserService;
import com.swivel.cc.base.service.CategoryService;
import com.swivel.cc.base.service.DealService;
import com.swivel.cc.base.service.ReportService;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Report controller
 */
@CrossOrigin
@Slf4j
@RestController
@Validated
@RequestMapping("api/v1/reports")
public class ReportController extends Controller {

    private final ReportService reportService;
    private final AuthUserService authUserService;
    private final DealService dealService;
    private final CategoryService categoryService;
    Logger logger = LoggerFactory.getLogger(ReportController.class);

    public ReportController(Translator translator, ReportService reportService,
                            AuthUserService authUserService, DealService dealService, CategoryService categoryService) {
        super(translator);
        this.reportService = reportService;
        this.authUserService = authUserService;
        this.dealService = dealService;
        this.categoryService = categoryService;
    }

    /**
     * Get top 10 deal view count list.
     *
     * @param userId     user Id
     * @param timeZone   time Zone
     * @param merchantId merchantId
     * @param option     date option
     * @return ResponseEntity
     */
    @GetMapping("/deal/audience-reach/top-ten/{merchantId}/{option}")
    public ResponseEntity<ResponseWrapper> getTopTenDealVsViewCount(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String merchantId,
            @PathVariable String option) {
        try {
            var dealViewCountReportResponse =
                    reportService.getTopDealsOrCategoryReport(GraphDateOption.getOption(option), merchantId,
                            timeZone, ReportType.DEAL);
            List<DealViewCountResponse> dealViewsList = dealService.getDealSetByIds(dealViewCountReportResponse);
            var merchantBusinessResponseDtoMap =
                    getBulkBusinessProfile(dealViewsList, userId);
            final Page<DealViewCountResponse> dealPage = new PageImpl<>(dealViewsList);
            var dealViewsListResponse = new TopTenDealViewsListResponseDto(dealPage, merchantBusinessResponseDtoMap);
            logger.debug("Successfully returned top 10 deal views graph for userId: {}.", userId);
            return getSuccessResponse(dealViewsListResponse, SuccessResponseStatusType.READ_DEAL_VIEW_GRAPH);
        } catch (InvalidDealException e) {
            logger.error("Invalid deal id from top 10 deal views graph. " +
                    "option: {}, user: {}", GraphDateOption.getOption(option), userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DEAL_ID);
        } catch (InvalidDateOptionException e) {
            logger.error("Invalid dateOption. dateOption: {}, userId: {}", option,
                    userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DATE_OPTION);
        } catch (QponCoreException e) {
            logger.error("Returning graph of top 10 deals was failed. option: {}, user: {}", option, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get deal view count list
     *
     * @param userId           userId
     * @param dealId           dealId
     * @param page             page
     * @param size             size
     * @param reportRequestDto reportRequestDto
     * @return audience reach report for merchant business profile
     */
    @PostMapping(value = "/deal/audience-reach/{dealId}/{page}/{size}")
    public ResponseEntity<ResponseWrapper> getDealVsViewCount(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable("dealId") String dealId,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @RequestBody ReportRequestDto reportRequestDto) {
        try {
            if (!reportRequestDto.isRequiredAvailableForReport()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            if (!reportRequestDto.isValidDateRange()) {
                return getErrorResponse(ErrorResponseStatusType.INVALID_DATE_RANGE);
            }
            if (!reportRequestDto.isSupportedDateRange()) {
                return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_OPTION_FOR_DATE_RANGE);
            }
            var pageable = PageRequest.of(page, size);
            var dealViewCountReportResponse =
                    reportService.getDealViewsReport(reportRequestDto, ALL, dealId, page, size, timeZone);
            List<DealViewCountResponse> dealViewsList = dealService.getDealSetByIds(dealViewCountReportResponse);
            var merchantBusinessResponseDtoMap =
                    getBulkBusinessProfile(dealViewsList, userId);
            final Page<DealViewCountResponse> dealPage = new PageImpl<>(dealViewsList, pageable, dealViewsList.size());
            var dealViewsListResponse = new DealViewsListResponseDto(dealPage, merchantBusinessResponseDtoMap);

            logger.debug("Successfully returned deal views count list for userId: {}.", userId);
            return getSuccessResponse(dealViewsListResponse, SuccessResponseStatusType.READ_DEAL_VIEW_LIST);
        } catch (QponCoreException e) {
            logger.error("Returning list of deal views count was failed. reportDateOption: {}, user: {}",
                    reportRequestDto.getOption(), userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method is used to get grouped audience reach report.
     *
     * @param userId           userId
     * @param merchantId       merchantId
     * @param page             page
     * @param size             size
     * @param reportRequestDto reportRequestDto
     * @return grouped audience reach report for merchant business profile.
     */
    @PostMapping(value = "/deal/grouped-audience-reach/{merchantId}/{page}/{size}")
    public ResponseEntity<ResponseWrapper> groupedAudienceReachReportForDeals(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable("merchantId") String merchantId,
            @Min(DEFAULT_PAGE) @PathVariable("page") int page,
            @Min(DEFAULT_PAGE) @Max(PAGE_MAX_SIZE) @Positive @PathVariable("size") int size,
            @RequestBody ReportRequestDto reportRequestDto) {
        try {
            if (reportRequestDto.isRequiredAvailable()) {
                if (!reportRequestDto.isValidDateRange()) {
                    return getErrorResponse(ErrorResponseStatusType.INVALID_DATE_RANGE);
                }
                Pageable pageable = PageRequest.of(page, size);
                var dealViewCountReportResponse =
                        reportService.getDealViewsReport(reportRequestDto, merchantId, ALL, page, size, timeZone);
                List<DealViewCountResponse> dealViewsList = dealService.getDealSetByIds(dealViewCountReportResponse);
                var merchantBusinessResponseDtoMap =
                        getBulkBusinessProfile(dealViewsList, userId);
                Page<DealViewCountResponse> dealPage = new PageImpl<>(dealViewsList, pageable, dealViewsList.size());
                var dealViewsListResponse = new GroupedDealViewsListResponseDto(dealPage, merchantBusinessResponseDtoMap);
                return getSuccessResponse(dealViewsListResponse, SuccessResponseStatusType.READ_DEAL_VIEW_LIST);
            } else {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
        } catch (QponCoreException e) {
            log.error("Getting grouped audience reach report for deals was failed. userId: {}", userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method gets bulk business profile for relevant deals.
     *
     * @param dealViewsList dealViewsList
     * @param userId        userId
     * @return bulk business profiles.
     */
    private Map<String, MerchantBusinessResponseDto> getBulkBusinessProfile(List<DealViewCountResponse> dealViewsList,
                                                                            String userId) {
        List<String> merchantIds = new ArrayList<>();
        Map<String, MerchantBusinessResponseDto> merchantBusinessResponseDtoMap = new HashMap<>();
        dealViewsList.forEach(s -> merchantIds.add(s.getDeal().getMerchantId()));
        if (!dealViewsList.isEmpty()) {
            merchantBusinessResponseDtoMap =
                    authUserService.getMerchantMap(userId, new BulkUserRequestDto(merchantIds), UserType.MERCHANT);
        }
        return merchantBusinessResponseDtoMap;
    }

    /**
     * This method is used to get grouped audience reach report for category.
     *
     * @param userId           userId
     * @param timeZone         timeZone
     * @param toUserId         toUserId
     * @param page             page
     * @param size             size
     * @param reportRequestDto reportRequestDto
     * @return category reach list.
     */
    @PostMapping(value = "/grouped-audience-reach/category/user/{toUserId}/{page}/{size}")
    public ResponseEntity<ResponseWrapper> groupedAudienceReachReportForCategory(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String toUserId,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Min(DEFAULT_PAGE) @Max(PAGE_MAX_SIZE) @Positive @PathVariable int size,
            @RequestBody ReportRequestDto reportRequestDto) {
        try {
            if (reportRequestDto.isRequiredAvailable()) {
                return getCategoryReportResponse(timeZone, toUserId, ALL, page, size, reportRequestDto);
            } else {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
        } catch (QponCoreException e) {
            log.error("Getting grouped audience reach report for category was failed. userId: {}", userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method is used to get audience reach report for category.
     *
     * @param userId           userId
     * @param timeZone         timeZone
     * @param categoryId       categoryId
     * @param page             page
     * @param size             size
     * @param reportRequestDto reportRequestDto
     * @return category reach list.
     */
    @PostMapping(value = "/audience-reach/category/{categoryId}/{page}/{size}")
    public ResponseEntity<ResponseWrapper> audienceReachReportForCategory(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String categoryId,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Min(DEFAULT_PAGE) @Max(PAGE_MAX_SIZE) @Positive @PathVariable int size,
            @RequestBody ReportRequestDto reportRequestDto) {
        try {
            if (reportRequestDto.isRequiredAvailableForReport()) {
                if (!reportRequestDto.isSupportedDateRange()) {
                    return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_OPTION_FOR_DATE_RANGE);
                }
                return getCategoryReportResponse(timeZone, ALL, categoryId, page, size, reportRequestDto);
            } else {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
        } catch (QponCoreException e) {
            log.error("Getting audience reach report for category was failed. userId: {}", userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This api is used to get top 10 category views list.
     *
     * @param userId   userId
     * @param timeZone timeZone
     * @param option   date option
     * @return top 10 category views.
     */
    @GetMapping("/category/audience-reach/top-ten/{option}")
    public ResponseEntity<ResponseWrapper> getTopTenCategoryReport(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String option) {
        try {
            var categoryViewCountReportResponse =
                    reportService.getTopDealsOrCategoryReport(GraphDateOption.getOption(option), ALL,
                            timeZone, ReportType.CATEGORY);
            List<CategoryViewCountResponseDto> categoryViewsList =
                    categoryService.getCategoriesForReport(categoryViewCountReportResponse, timeZone);
            Page<CategoryViewCountResponseDto> categoryPage = new PageImpl<>(categoryViewsList);
            var categoryReachResponseDto = new CategoryAudienceReachResponseDto(categoryPage, categoryViewsList);
            logger.debug("Successfully returned top 10 category views for userId: {}.", userId);
            return getSuccessResponse(categoryReachResponseDto, SuccessResponseStatusType.CATEGORY_REACH_LIST);
        } catch (InvalidDateOptionException e) {
            logger.error("Invalid dateOption. dateOption: {}, userId: {}", option, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_DATE_OPTION);
        } catch (QponCoreException e) {
            logger.error("Returning top 10 categories was failed. option: {}, user: {}.", option, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * This method is used to create category report response.
     *
     * @param timeZone         timeZone
     * @param toUserId         toUserId
     * @param categoryId       categoryId
     * @param page             page
     * @param size             size
     * @param reportRequestDto reportRequestDto
     * @return category report response.
     */
    private ResponseEntity<ResponseWrapper> getCategoryReportResponse(String timeZone, String toUserId,
                                                                      String categoryId, int page, int size,
                                                                      ReportRequestDto reportRequestDto) {
        if (!reportRequestDto.isValidDateRange()) {
            return getErrorResponse(ErrorResponseStatusType.INVALID_DATE_RANGE);
        }
        Pageable pageable = PageRequest.of(page, size);
        var categoryViewCountReportResponse =
                reportService.getCategoryReport(reportRequestDto, toUserId, categoryId, page, size, timeZone);
        List<CategoryViewCountResponseDto> categoryViewsList =
                categoryService.getCategoriesForReport(categoryViewCountReportResponse, timeZone);
        Page<CategoryViewCountResponseDto> categoryPage =
                new PageImpl<>(categoryViewsList, pageable, categoryViewsList.size());
        var categoryReachResponseDto = new CategoryAudienceReachResponseDto(categoryPage, categoryViewsList);
        return getSuccessResponse(categoryReachResponseDto, SuccessResponseStatusType.CATEGORY_REACH_LIST);
    }
}
