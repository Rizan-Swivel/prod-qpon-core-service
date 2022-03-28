package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.request.CategoryBrandMerchantRequestDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Categories for Merchant
 */
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "category_brand_merchant")
public class CategoryBrandMerchant {

    @Transient
    private static final String CATEGORY_MERCHANT_ID_PREFIX = "cbmid-";

    @Id
    private String id;
    private String merchantId;
    private long createdAt;
    private long updatedAt;
    @ElementCollection
    private Set<String> categories = new HashSet<>();
    @ElementCollection
    private Set<String> brands = new HashSet<>();
    private boolean isActiveMerchant;
    @Enumerated(EnumType.STRING)
    private ApprovalStatus merchantApprovalStatus;
    @Enumerated(EnumType.STRING)
    private UserType userType;

    public CategoryBrandMerchant(CategoryBrandMerchantRequestDto categoryBrandMerchantRequestDto,
                                 ApprovalStatus approvalStatus, boolean isActiveMerchant, UserType userType) {
        this.id = CATEGORY_MERCHANT_ID_PREFIX + UUID.randomUUID();
        this.merchantId = categoryBrandMerchantRequestDto.getMerchantId();
        this.categories.addAll(categoryBrandMerchantRequestDto.getCategories());
        if (categoryBrandMerchantRequestDto.isBrandIdsAvailable())
            this.brands.addAll(categoryBrandMerchantRequestDto.getBrands());
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.merchantApprovalStatus = approvalStatus;
        this.isActiveMerchant = isActiveMerchant;
        this.userType = userType;
    }
}
