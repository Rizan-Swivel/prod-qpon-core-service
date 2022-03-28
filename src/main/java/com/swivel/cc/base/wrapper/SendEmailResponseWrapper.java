package com.swivel.cc.base.wrapper;


import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.Getter;
import lombok.Setter;

/**
 * Send email response wrapper to get email sent response from util service
 */
@Getter
@Setter
public class SendEmailResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private String displayMessage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
