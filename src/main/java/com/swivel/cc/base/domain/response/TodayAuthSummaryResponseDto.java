package com.swivel.cc.base.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * Today auth summary response Dto
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class TodayAuthSummaryResponseDto extends ResponseDto {

    private long noOfNewMerchants;
    private long totalMerchants;
    private long totalActiveMerchants;
    private long noOfNewMobileUsers;
    private long totalMobileUsers;
    private long noOfNewBanks;
    private long totalBanks;
    private long totalActiveBanks;
}
