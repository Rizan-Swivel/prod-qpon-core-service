package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.domain.response.TodayAuthSummaryResponseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.Getter;
import lombok.Setter;

/**
 * Response wrapper for today summary.
 */
@Getter
@Setter
public class TodayAuthSummaryResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private TodayAuthSummaryResponseDto data;
    private String displayMessage;


    /**
     * This method converts object to json string for logging purpose.
     * PII data should be obfuscated.
     *
     * @return json string
     */
    @Override
    public String toLogJson() {
        return toJson();
    }
}
