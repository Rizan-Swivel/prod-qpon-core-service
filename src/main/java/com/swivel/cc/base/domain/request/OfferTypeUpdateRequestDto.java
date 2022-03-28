package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OfferTypeUpdateRequestDto extends RequestDto {

    private String id;
    private String name;

    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(name) && isNonEmpty(id);
    }
}
