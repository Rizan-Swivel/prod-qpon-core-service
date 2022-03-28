package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.domain.response.ResponseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.Getter;

/**
 * ResponseWrapper
 */
@Getter
public class ResponseWrapper implements BaseDto {

    private final ResponseStatusType status;
    private final String message;
    private final ResponseDto data;
    private final String displayMessage;

    /**
     * @param status         status
     * @param message        developer message
     * @param data           data
     * @param displayMessage displayMessage
     */
    public ResponseWrapper(ResponseStatusType status, String message, ResponseDto data, String displayMessage) {
        this.status = status;
        this.message = message;
        this.data = data;
        this.displayMessage = displayMessage;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}

