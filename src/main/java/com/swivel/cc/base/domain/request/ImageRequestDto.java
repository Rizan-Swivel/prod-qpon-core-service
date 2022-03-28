package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

/**
 * Image request Dto for get category images
 */

@Setter
@Getter
public class ImageRequestDto extends RequestDto {

    private String activeImageUrl;
    private String inactiveImageUrl;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
