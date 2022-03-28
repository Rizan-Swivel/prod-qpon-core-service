package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.BaseDto;

/**
 * ResponseDto - All responseDto classes are needed to implement this class.
 */
public abstract class ResponseDto implements BaseDto {
    @Override
    public String toLogJson() {
        return toJson();
    }
}
