package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
public class CategoryResponseDto extends ResponseDto {

    private String id;
    private String name;
    private String description;
    private String imageUrl;
    private CategoryType categoryType;
    private Long expiryDate;
    private DateResponseDto createdAt;
    private DateResponseDto updatedAt;
    private boolean isPopular;

    public CategoryResponseDto(Category category, String timeZone) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.categoryType = category.getCategoryType();
        this.expiryDate = category.getExpiryDate();
        this.createdAt = new DateResponseDto(category.getCreatedAt(), timeZone, new Date(category.getCreatedAt()));
        this.updatedAt = new DateResponseDto(category.getUpdatedAt(), timeZone, new Date(category.getUpdatedAt()));
        this.isPopular = category.isPopular();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
