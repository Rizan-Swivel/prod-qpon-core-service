package com.swivel.cc.base.service;

import com.google.analytics.data.v1beta.*;
import com.swivel.cc.base.domain.request.ReportRequestDto;
import com.swivel.cc.base.domain.response.ReportDateResponseDto;
import com.swivel.cc.base.domain.response.ViewCountAnalyticResponseDto;
import com.swivel.cc.base.enums.GraphDateOption;
import com.swivel.cc.base.enums.ReportDateOption;
import com.swivel.cc.base.enums.ReportQueryAttribute;
import com.swivel.cc.base.enums.ReportType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.util.DateRangeConverter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
public class ReportService {

    private static final String PROPERTY = "properties/";
    private static final String ANALYTICS_DATE_FORMAT = "yyyy-MM-dd";
    private static final String ALL = "ALL";
    private static final int ZERO_ELEMENT = 0;
    private static final int FIRST_ELEMENT = 1;
    private static final int SECOND_ELEMENT = 2;
    private static final int THREE = 3;
    private final String propertyId;
    private final int topViewsCount;
    private final BetaAnalyticsDataClient analyticsData;

    @Autowired
    public ReportService(@Value("${analytics.propertyId}") String propertyId,
                         @Value("${analytics.topViewsCount}") int topViewsCount,
                         BetaAnalyticsDataClient analyticsData) {
        this.propertyId = propertyId;
        this.topViewsCount = topViewsCount;
        this.analyticsData = analyticsData;
    }

    /**
     * This method gives top 10 deal report response by querying
     *
     * @param graphDateOption date range enum value
     * @param merchantId      merchant Id
     * @param timeZone        time zone
     * @return GetReportsResponse
     */
    public List<ViewCountAnalyticResponseDto> getTopDealsOrCategoryReport(GraphDateOption graphDateOption,
                                                                          String merchantId, String timeZone,
                                                                          ReportType reportType) {

        String startDate = new DateRangeConverter(timeZone, graphDateOption).getStartDate();
        String endDate = new DateRangeConverter(timeZone, graphDateOption).getEndDate();
        ReportQueryAttribute reportQueryAttribute = reportType.equals(ReportType.DEAL) ?
                ReportQueryAttribute.DEAL_ID_DIMENSION : ReportQueryAttribute.CATEGORY_ID_DIMENSION;
        var orderBy = OrderBy.newBuilder()
                .setDesc(true)
                .setMetric(OrderBy.MetricOrderBy.newBuilder()
                        .setMetricName(ReportQueryAttribute.EVENT_COUNT_METRIC.getQueryAttribute()));

        var request =
                createAnalyticsRequestBuilder(startDate, endDate, topViewsCount, reportQueryAttribute);
        if (reportType.equals(ReportType.DEAL))
            request.addDimensions(Dimension.newBuilder()
                    .setName(ReportQueryAttribute.MERCHANT_ID_DIMENSION.getQueryAttribute()));
        if (!merchantId.equals(ALL))
            request = addFilterForReportRequest(request, merchantId, ReportQueryAttribute.MERCHANT_ID_DIMENSION);

        request.addOrderBys(orderBy);
        RunReportResponse response = analyticsData.runReport(request.build());
        if (reportType.equals(ReportType.DEAL))
            return createDealViewCountAnalyticResponseDtoEntity(response, null,
                    dateToTimestamp(startDate), dateToTimestamp(endDate), timeZone);
        else
            return createCategoryViewCountAnalyticResponseDtoEntity(response, null,
                    dateToTimestamp(startDate), dateToTimestamp(endDate), timeZone);
    }

    /**
     * This method generates deals report by querying google analytics.
     *
     * @param merchantId merchantId
     * @param dealId     dealId
     * @param page       page
     * @param size       size
     * @param timeZone   timeZone
     * @return GetReportsResponse
     */
    public List<ViewCountAnalyticResponseDto> getDealViewsReport(ReportRequestDto reportRequestDto,
                                                                 String merchantId, String dealId, int page,
                                                                 int size, String timeZone) {

        ReportDateOption dateOption = reportRequestDto.getOption();
        long startDate = reportRequestDto.getStartDate();
        long endDate = reportRequestDto.getEndDate();

        var request = createAnalyticsRequestBuilder(
                dateFormatter(startDate), dateFormatter(endDate), size, ReportQueryAttribute.DEAL_ID_DIMENSION);

        request.addDimensions(Dimension.newBuilder().setName(
                        ReportQueryAttribute.MERCHANT_ID_DIMENSION.getQueryAttribute()))
                .setOffset(page);
        if (dateOption != null)
            request = addDateOptionAndOrderByToReportRequest(dateOption, request);
        if (!merchantId.equals(ALL))
            request = addFilterForReportRequest(request, merchantId, ReportQueryAttribute.MERCHANT_ID_DIMENSION);
        if (!dealId.equals(ALL))
            request = addFilterForReportRequest(request, dealId, ReportQueryAttribute.DEAL_ID_DIMENSION);

        RunReportResponse response = analyticsData.runReport(request.build());

        return createDealViewCountAnalyticResponseDtoEntity(response, dateOption, startDate, endDate, timeZone);
    }

    /**
     * This method will add dateOption & orderBy to the analytics report request.
     *
     * @param dateOption report date option.
     * @param request    analytics request.
     * @return analytics request with orderBy query & date dimension.
     */
    private RunReportRequest.Builder addDateOptionAndOrderByToReportRequest(ReportDateOption dateOption,
                                                                            RunReportRequest.Builder request) {
        var orderBy = OrderBy.newBuilder()
                .setDesc(true)
                .setDimension(OrderBy.DimensionOrderBy.newBuilder()
                        .setDimensionName(dateOption.getAnalyticsOption()));
        return request.addDimensions(Dimension.newBuilder().setName(dateOption.getAnalyticsOption()))
                .addOrderBys(orderBy);
    }

    /**
     * This method generates categories report by querying google analytics.
     *
     * @param reportRequestDto reportRequestDto
     * @param userId           userId
     * @param categoryId       userId
     * @param page             page
     * @param size             size
     * @param timeZone         timeZone
     * @return categoryIds with view count.
     */
    public List<ViewCountAnalyticResponseDto> getCategoryReport(ReportRequestDto reportRequestDto, String userId,
                                                                String categoryId, int page, int size,
                                                                String timeZone) {
        ReportDateOption dateOption = reportRequestDto.getOption();
        long startDate = reportRequestDto.getStartDate();
        long endDate = reportRequestDto.getEndDate();
        var request = createAnalyticsRequestBuilder(
                dateFormatter(startDate), dateFormatter(endDate), size, ReportQueryAttribute.CATEGORY_ID_DIMENSION);
        request.setOffset(page);

        if (dateOption != null)
            request = addDateOptionAndOrderByToReportRequest(dateOption, request);
        if (!userId.equals(ALL))
            request = addFilterForReportRequest(request, userId, ReportQueryAttribute.MERCHANT_ID_DIMENSION);
        if (!categoryId.equals(ALL))
            request = addFilterForReportRequest(request, categoryId, ReportQueryAttribute.CATEGORY_ID_DIMENSION);

        RunReportResponse response = analyticsData.runReport(request.build());
        return createCategoryViewCountAnalyticResponseDtoEntity(response, dateOption, startDate, endDate, timeZone);
    }

    /**
     * This method is used to create analytics request builder.
     *
     * @param startDate      startDate
     * @param endDate        endDate
     * @param size           size
     * @param queryDimension queryDimension
     * @return analytics request builder
     */
    private RunReportRequest.Builder createAnalyticsRequestBuilder(String startDate, String endDate, int size,
                                                                   ReportQueryAttribute queryDimension) {
        return RunReportRequest.newBuilder()
                .setProperty(PROPERTY + propertyId)
                .addDateRanges(DateRange.newBuilder().setStartDate(startDate).setEndDate(endDate))
                .addDimensions(Dimension.newBuilder()
                        .setName(queryDimension.getQueryAttribute()))
                .addMetrics(Metric.newBuilder()
                        .setName(ReportQueryAttribute.EVENT_COUNT_METRIC.getQueryAttribute()))
                .setLimit(size);
    }

    /**
     * This method is used to add filter for analytics report.
     *
     * @param request        analytics request builder
     * @param filteringValue filteringValue
     * @param queryDimension queryDimension
     * @return analytics request builder with filter
     */
    private RunReportRequest.Builder addFilterForReportRequest(RunReportRequest.Builder request, String filteringValue,
                                                               ReportQueryAttribute queryDimension) {
        var filter = FilterExpression.newBuilder()
                .setFilter(Filter.newBuilder()
                        .setFieldName(queryDimension.getQueryAttribute())
                        .setStringFilter(Filter.StringFilter.newBuilder().setValue(filteringValue)));
        return request.setDimensionFilter(filter);
    }

    /**
     * This method returns view count for deals using analytics report response.
     *
     * @param response         response
     * @param reportDateOption reportDateOption
     * @param startDate        start date
     * @param endDate          end date
     * @param timeZone         timeZone
     * @return list of deals view counts.
     */
    private List<ViewCountAnalyticResponseDto>
    createDealViewCountAnalyticResponseDtoEntity(RunReportResponse response, ReportDateOption reportDateOption,
                                                 long startDate, long endDate, String timeZone) {
        List<ViewCountAnalyticResponseDto> dealViewsList = new ArrayList<>();
        for (Row row : response.getRowsList()) {
            var dealViews = new ViewCountAnalyticResponseDto(row.getDimensionValues(ZERO_ELEMENT).getValue(),
                    row.getDimensionValues(FIRST_ELEMENT).getValue());
            if (row.getDimensionValuesCount() == THREE) {
                ReportDateResponseDto displayDate = new ReportDateResponseDto(reportDateOption,
                        row.getDimensionValues(SECOND_ELEMENT).getValue(), startDate, endDate, timeZone);
                dealViews.setDisplayDate(displayDate.getDisplayDate());
            }
            dealViews.setViewCount(Long.parseLong(row.getMetricValues(ZERO_ELEMENT).getValue()));
            dealViewsList.add(dealViews);
        }
        return dealViewsList;
    }

    /**
     * This method returns view count for categories using analytics report response.
     *
     * @param response         analytics response
     * @param reportDateOption reportDateOption
     * @param startDate        startDate
     * @param endDate          endDate
     * @param timeZone         timeZone
     * @return list of categories view counts.
     */
    private List<ViewCountAnalyticResponseDto>
    createCategoryViewCountAnalyticResponseDtoEntity(RunReportResponse response, ReportDateOption reportDateOption,
                                                     long startDate, long endDate, String timeZone) {
        List<ViewCountAnalyticResponseDto> dealViewsList = new ArrayList<>();
        for (Row row : response.getRowsList()) {
            var dealViews = new ViewCountAnalyticResponseDto(row.getDimensionValues(ZERO_ELEMENT).getValue(),
                    Long.parseLong(row.getMetricValues(ZERO_ELEMENT).getValue()));
            if (row.getDimensionValuesCount() == SECOND_ELEMENT) {
                ReportDateResponseDto displayDate = new ReportDateResponseDto(reportDateOption,
                        row.getDimensionValues(FIRST_ELEMENT).getValue(), startDate, endDate, timeZone);
                dealViews.setDisplayDate(displayDate.getDisplayDate());
            }
            dealViewsList.add(dealViews);
        }
        return dealViewsList;
    }

    /**
     * This method converts sting dates to timestamp.
     *
     * @param dateInString dateInString
     * @return timestamp
     */
    private long dateToTimestamp(String dateInString) {
        try {
            DateFormat formatter = new SimpleDateFormat(ANALYTICS_DATE_FORMAT);
            Date date = formatter.parse(dateInString);
            return date.getTime();
        } catch (ParseException e) {
            throw new QponCoreException("Date to timestamp conversion failed", e);
        }
    }

    /**
     * This method is used to format date to support Google Analytics.
     *
     * @param timeStamp timeStamp
     * @return date in string
     */
    private String dateFormatter(long timeStamp) {
        var date = new Date(timeStamp);
        var simpleDateFormat = new SimpleDateFormat(ANALYTICS_DATE_FORMAT);
        return simpleDateFormat.format(date);
    }
}
