package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request dto for brand
 */
@Setter
@Getter
public class BrandRequestDto extends RequestDto {
    private String name;
    private String description;
    private String imageUrl;


    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(name) && isNonEmpty(description);
    }
}
