package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferTypeCreateRequestDto extends RequestDto {

    String name;


    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(name);
    }
}
