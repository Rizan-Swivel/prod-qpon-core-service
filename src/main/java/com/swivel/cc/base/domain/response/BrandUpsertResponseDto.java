package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * Response dto for create brand
 */
@Getter
@Setter
public class BrandUpsertResponseDto extends ResponseDto {
    private final String id;
    private final String name;
    private final String description;
    private final DateResponseDto createdAt;
    private final DateResponseDto updatedAt;
    private String imageUrl;

    public BrandUpsertResponseDto(Brand brand, String timeZone) {
        this.id = brand.getId();
        this.name = brand.getName();
        this.description = brand.getDescription();
        this.imageUrl = brand.getImageUrl();
        this.createdAt = new DateResponseDto(brand.getCreatedAt(), timeZone, new Date(brand.getCreatedAt()));
        this.updatedAt = new DateResponseDto(brand.getUpdatedAt(), timeZone, new Date(brand.getUpdatedAt()));
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
