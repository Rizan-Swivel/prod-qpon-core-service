package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * Categories for merchant request dto
 */
@Setter
@Getter
public class CategoryBrandMerchantRequestDto extends RequestDto {

    private String merchantId;
    private List<String> categories;
    private List<String> brands;

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(merchantId) && categories != null && !categories.isEmpty();
    }

    /**
     * This method checks the brand id is available or not.
     *
     * @return true / false
     */
    public boolean isBrandIdsAvailable() {
        return brands != null && !brands.isEmpty();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
