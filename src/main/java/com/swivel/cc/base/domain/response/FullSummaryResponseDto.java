package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Full summary response Dto
 */
@Getter
@Setter
public class FullSummaryResponseDto extends ResponseDto {

    private TodaySummaryResponseDto todaySummary;
    private SummaryResponseDto summary;

    public FullSummaryResponseDto(TodaySummaryResponseDto todaySummary, SummaryResponseDto summary) {
        this.todaySummary = todaySummary;
        this.summary = summary;
    }
}
