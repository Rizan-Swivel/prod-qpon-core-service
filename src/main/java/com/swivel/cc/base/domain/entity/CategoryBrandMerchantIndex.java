package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * This class should replace from a search engine - real-time update
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category_brand_merchant_index")
public class CategoryBrandMerchantIndex {
    @Transient
    private static final int CATEGORY_IDS_MAX_LENGTH = 1000;
    @Transient
    private static final int BRAND_IDS_MAX_LENGTH = 1000;

    @Id
    private String id;
    private String merchantId;
    private long createdAt;
    private long updatedAt;
    @Size(max = CATEGORY_IDS_MAX_LENGTH)
    private String categoryIds;
    @Size(max = BRAND_IDS_MAX_LENGTH)
    private String brandIds;
    private String merchantName;
    private String merchantImageUrl;
    private boolean isActiveMerchant;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus merchantApprovalStatus;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public CategoryBrandMerchantIndex(CategoryBrandMerchant categoryBrandMerchant,
                                      BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto) {
        this.id = categoryBrandMerchant.getId();
        this.merchantId = categoryBrandMerchant.getMerchantId();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.categoryIds = categoryBrandMerchant.getCategories().toString();
        this.brandIds = categoryBrandMerchant.getBrands().toString();
        this.merchantName = basicMerchantBusinessResponseDto.getName();
        this.merchantImageUrl = basicMerchantBusinessResponseDto.getImageUrl();
        this.isActiveMerchant = basicMerchantBusinessResponseDto.isActive();
        this.merchantApprovalStatus = basicMerchantBusinessResponseDto.getApprovalStatus();
        this.userType = categoryBrandMerchant.getUserType();
    }
}
