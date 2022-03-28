package com.swivel.cc.base.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Date;

/**
 * Today summary response Dto
 */
@Getter
@Setter
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class TodaySummaryResponseDto extends ResponseDto {

    private String title = "Today's Summary";
    private String subTitle;
    private Long noOfNewBanks;
    private Long noOfNewMerchants;
    private Long noOfNewMobileUsers;
    private Long noOfNewDeals;
    private Long noOfExpiringDeals;
    private Long noOfNewDealRequests;
    private Long noOfNewCreditCardRequests;

    public TodaySummaryResponseDto(long noOfNewDeals, long noOfExpiringDeals, long noOfNewDealRequests,
                                   Long noOfNewCreditCardRequests) {
        this.noOfNewDeals = noOfNewDeals;
        this.noOfExpiringDeals = noOfExpiringDeals;
        this.noOfNewDealRequests = noOfNewDealRequests;
        this.noOfNewCreditCardRequests = noOfNewCreditCardRequests;
    }

    /**
     * This method will set attributes for admin response.
     *
     * @param todayAuthSummaryResponseDto todayAuthSummaryResponseDto
     */
    public void setAdminResponse(TodayAuthSummaryResponseDto todayAuthSummaryResponseDto) {
        this.noOfNewMerchants = todayAuthSummaryResponseDto.getNoOfNewMerchants();
        this.noOfNewMobileUsers = todayAuthSummaryResponseDto.getNoOfNewMobileUsers();
        this.noOfNewBanks = todayAuthSummaryResponseDto.getNoOfNewBanks();
    }

    /**
     * This method will set attributes for bank response.
     *
     * @param todayAuthSummaryResponseDto todayAuthSummaryResponseDto
     */
    public void setBankResponse(TodayAuthSummaryResponseDto todayAuthSummaryResponseDto) {
        this.noOfNewMerchants = todayAuthSummaryResponseDto.getNoOfNewMerchants();
        this.noOfNewMobileUsers = todayAuthSummaryResponseDto.getNoOfNewMobileUsers();
    }

    /**
     * This method will set subTitle.
     *
     * @param timestamp  timestamp
     * @param timezoneId timezoneId
     * @param date       date
     */
    public void setSubTitle(long timestamp, String timezoneId, Date date) {
        this.subTitle = new DateResponseDto(timestamp, timezoneId, date).getDisplayDate();
    }
}
