package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.domain.response.MobileNoResponseDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser implements BaseDto {

    private String fullName;
    private String email;
    private String imageUrl;
    private MobileNoResponseDto mobileNo;
    private String language;
    private boolean registeredUser;
    private ApprovalStatus approvalStatus;
    private boolean isActive;

    public AuthUser(String fullName, String imageUrl) {
        this.fullName = fullName;
        this.imageUrl = imageUrl;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
