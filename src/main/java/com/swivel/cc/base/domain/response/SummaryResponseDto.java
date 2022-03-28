package com.swivel.cc.base.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.time.LocalDate;
import java.time.ZoneId;

/**
 * Summary response Dto
 */
@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class SummaryResponseDto extends ResponseDto {

    private static final String APP_SUMMARY = "App Summary";
    private static final String MY_SUMMARY = "My Summary";
    private static final String JOIN = " - ";
    private String title;
    private String subTitle;
    private Long totalMerchants;
    private Long totalActiveMerchants;
    private Long totalBanks;
    private Long totalActiveBanks;
    private Long totalMobileUsers;
    private Long totalCategories;
    private Long totalActiveCategories;
    private Long totalBrands;
    private Long totalBankDeals;
    private Long totalMerchantDeals;
    private Long totalDeals;
    private Long totalActiveDeals;
    private Long totalNoOfDealRequests;
    private Long totalNoOfCreditCardRequest;

    public SummaryResponseDto(long totalCategories, long totalActiveCategories, long totalBrands, long totalDeals,
                              long totalActiveDeals, long totalNoOfDealRequests) {
        this.totalCategories = totalCategories;
        this.totalActiveCategories = totalActiveCategories;
        this.totalBrands = totalBrands;
        this.totalDeals = totalDeals;
        this.totalActiveDeals = totalActiveDeals;
        this.totalNoOfDealRequests = totalNoOfDealRequests;
    }

    /**
     * This method will set attributes for admin response.
     *
     * @param todayAuthSummaryResponseDto todayAuthSummaryResponseDto
     */
    public void setAdminResponse(TodayAuthSummaryResponseDto todayAuthSummaryResponseDto) {
        this.title = APP_SUMMARY;
        this.totalMerchants = todayAuthSummaryResponseDto.getTotalMerchants();
        this.totalActiveMerchants = todayAuthSummaryResponseDto.getTotalActiveMerchants();
        this.totalMobileUsers = todayAuthSummaryResponseDto.getTotalMobileUsers();
        this.totalBanks = todayAuthSummaryResponseDto.getTotalBanks();
        this.totalActiveBanks = todayAuthSummaryResponseDto.getTotalActiveBanks();
    }

    /**
     * This method will set attributes for merchant response.
     *
     * @param joinedDate joinedDate
     * @param timeZone   timeZone
     */
    public void setTitleAndSubTitleResponse(String joinedDate, String timeZone) {
        this.title = MY_SUMMARY;
        LocalDate today = LocalDate.now(ZoneId.of(timeZone));
        long todayInMillis = today.atStartOfDay(ZoneId.of(timeZone)).toInstant().toEpochMilli();
        String todayDate = new DateResponseDto(todayInMillis, timeZone, Date.valueOf(today)).getDisplayDate();
        this.subTitle = joinedDate + JOIN + todayDate;
    }
}
