package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.response.ResponseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import lombok.Getter;

/**
 * Success Response Wrapper
 */
@Getter
public class SuccessResponseWrapper extends ResponseWrapper {

    private final int statusCode;

    public SuccessResponseWrapper(ResponseStatusType status, SuccessResponseStatusType successResponseStatusType, ResponseDto responseDto, String displayMessage) {
        super(status, successResponseStatusType.getMessage(), responseDto, displayMessage);
        this.statusCode = successResponseStatusType.getCode();
    }
}
