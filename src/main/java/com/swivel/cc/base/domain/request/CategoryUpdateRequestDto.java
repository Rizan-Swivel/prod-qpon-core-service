package com.swivel.cc.base.domain.request;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.swivel.cc.base.enums.CategoryType;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Request dto for category update
 */
@Setter
@Getter
public class CategoryUpdateRequestDto extends RequestDto {

    private String id;
    private String name;
    private String description;
    private CategoryType categoryType;
    private String imageUrl;
    private Long expiryDate;
    private List<String> relatedCategories;
    @JsonProperty
    private boolean isPopular;

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(id) && isNonEmpty(name)
                && isNonEmpty(description) && isNonEmpty(categoryType.name()) && isNonEmpty(imageUrl);
    }

    /**
     * This method checks expiryDate is a future date or not
     *
     * @return true / false
     */
    public boolean isValidExpiryDate() {
        if (expiryDate != null) {
            return expiryDate > System.currentTimeMillis();
        } else {
            return true;
        }
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
