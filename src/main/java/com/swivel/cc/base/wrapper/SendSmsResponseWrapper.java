package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.Getter;
import lombok.Setter;

/**
 * Send SMS response wrapper to get SMS sent response from util service
 */
@Getter
@Setter
public class SendSmsResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private String displayMessage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
