package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.AuthUser;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchantIndex;
import com.swivel.cc.base.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Merchant response Dto
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class BasicMerchantBusinessResponseDto extends ResponseDto {
    private String id;
    private String name;
    private String imageUrl;
    private ApprovalStatus approvalStatus;
    private boolean isActive;
    private String userType;

    public BasicMerchantBusinessResponseDto(String id, AuthUser authUser) {
        this.id = id;
        this.name = authUser.getFullName();
        this.imageUrl = authUser.getImageUrl();
        this.approvalStatus = authUser.getApprovalStatus();
        this.isActive = authUser.isActive();
    }

    public BasicMerchantBusinessResponseDto(CategoryBrandMerchantIndex categoryBrandMerchantIndex) {
        this.id = categoryBrandMerchantIndex.getMerchantId();
        this.name = categoryBrandMerchantIndex.getMerchantName();
        this.imageUrl = categoryBrandMerchantIndex.getMerchantImageUrl();
        this.approvalStatus = categoryBrandMerchantIndex.getMerchantApprovalStatus();
        this.isActive = categoryBrandMerchantIndex.isActiveMerchant();
    }

    public BasicMerchantBusinessResponseDto(BusinessMerchantResponseDto businessMerchantResponseDto) {
        this.id = businessMerchantResponseDto.getMerchantId();
        this.name = businessMerchantResponseDto.getBusinessName();
        this.imageUrl = businessMerchantResponseDto.getImageUrl();
        this.isActive = businessMerchantResponseDto.isActive();
        this.approvalStatus = businessMerchantResponseDto.getApprovalStatus();
        this.userType = businessMerchantResponseDto.getProfileType();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}


