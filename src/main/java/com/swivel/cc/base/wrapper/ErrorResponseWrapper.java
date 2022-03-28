package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.response.ResponseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.Getter;

/**
 * ErrorResponseWrapper
 */
@Getter
public class ErrorResponseWrapper extends ResponseWrapper {

    private final int errorCode;

    public ErrorResponseWrapper(ResponseStatusType status, String message, ResponseDto data, String displayMessage, int errorCode) {
        super(status, message, data, displayMessage);
        this.errorCode = errorCode;
    }

}
