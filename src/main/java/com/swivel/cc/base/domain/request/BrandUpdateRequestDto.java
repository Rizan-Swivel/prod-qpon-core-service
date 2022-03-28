package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Request dto for brand update
 */
@Setter
@Getter
public class BrandUpdateRequestDto extends RequestDto {
    private String id;
    private String name;
    private String description;
    private String imageUrl;


    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(id) && isNonEmpty(name) && isNonEmpty(description);
    }
}
