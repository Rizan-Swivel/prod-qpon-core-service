package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * Response dto for Update category
 */
@Getter
@Setter
public class CategoryUpdateResponseDto extends ResponseDto {

    private final String id;
    private final String name;
    private final String description;
    private final CategoryType categoryType;
    private final Long expiryDate;
    private final DateResponseDto createdAt;
    private final DateResponseDto updatedAt;
    private String imageUrl;
    private boolean isPopular;

    public CategoryUpdateResponseDto(Category category, String timeZone) {
        this.id = category.getId();
        this.name = category.getName();
        this.description = category.getDescription();
        this.imageUrl = category.getImageUrl();
        this.categoryType = category.getCategoryType();
        this.expiryDate = category.getExpiryDate();
        this.isPopular = category.isPopular();
        this.createdAt = new DateResponseDto(category.getCreatedAt(), timeZone, new Date(category.getCreatedAt()));
        this.updatedAt = new DateResponseDto(category.getUpdatedAt(), timeZone, new Date(category.getUpdatedAt()));
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
