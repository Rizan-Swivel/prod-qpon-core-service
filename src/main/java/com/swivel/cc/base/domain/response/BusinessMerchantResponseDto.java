package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BusinessMerchantResponseDto extends ResponseDto {
    private String businessId;
    private String merchantId;
    private String businessName;
    private String ownerName;
    private ApprovalStatus approvalStatus;
    private MobileNoResponseDto telephone;
    private String businessRegNo;
    private String address;
    private String email;
    private String imageUrl;
    private String webSite;
    private String facebook;
    private String instagram;
    private DateResponseDto createdAt;
    private DateResponseDto updatedAt;
    private boolean active;
    private String profileType;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
