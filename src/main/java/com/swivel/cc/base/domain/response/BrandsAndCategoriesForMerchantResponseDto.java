package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CategoryBrandMerchant;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Categories for merchant response dto
 */
@Getter
@Setter
public class BrandsAndCategoriesForMerchantResponseDto extends ResponseDto {
    private final Set<BasicCategoryResponseDto> categories = new HashSet<>();
    private final Set<BasicBrandResponseDto> brands = new HashSet<>();
    private String id;
    private String merchantId;
    private DateResponseDto createdAt;
    private DateResponseDto updatedAt;

    public BrandsAndCategoriesForMerchantResponseDto(CategoryBrandMerchant categoryBrandMerchant,
                                                     Set<BasicCategoryResponseDto> basicCategoryResponseDtoSet,
                                                     Set<BasicBrandResponseDto> basicBrandResponseDtoSet,
                                                     String timeZone) {
        this.id = categoryBrandMerchant.getId();
        this.merchantId = categoryBrandMerchant.getMerchantId();
        this.categories.addAll(basicCategoryResponseDtoSet);
        this.brands.addAll(basicBrandResponseDtoSet);
        this.createdAt = new DateResponseDto(categoryBrandMerchant.getCreatedAt(), timeZone,
                new Date(categoryBrandMerchant.getCreatedAt()));
        this.updatedAt = new DateResponseDto(categoryBrandMerchant.getUpdatedAt(), timeZone,
                new Date(categoryBrandMerchant.getUpdatedAt()));
    }
}
