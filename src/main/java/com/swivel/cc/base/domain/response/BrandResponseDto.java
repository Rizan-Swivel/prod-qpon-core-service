package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * Response Dto for Brand
 */
@Getter
@Setter
public class BrandResponseDto extends ResponseDto {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private DateResponseDto createdAt;
    private DateResponseDto updatedAt;

    public BrandResponseDto(Brand brand, String timeZone) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.description = brand.getDescription();
        this.imageUrl = brand.getImageUrl();
        this.createdAt = new DateResponseDto(brand.getCreatedAt(), timeZone, new Date(brand.getCreatedAt()));
        this.updatedAt = new DateResponseDto(brand.getUpdatedAt(), timeZone, new Date(brand.getUpdatedAt()));
    }
}

