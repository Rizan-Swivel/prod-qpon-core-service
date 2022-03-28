package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.UserType;
import lombok.Getter;

@Getter
public class BasicBankResponseDto {

    private String id;
    private String name;
    private String imageUrl;
    private ApprovalStatus approvalStatus;
    private boolean isActive;

    public BasicBankResponseDto(int i) {
        setBank(i);
    }

    public BasicBankResponseDto(UserType userType, int i) {
        setMerchant(i);
    }

    private void setBank(int i) {
        if (i == 0) {
            this.id = "uid-e41fc0a1-e3f9-4074-9049-fcaf10b16c14";
            this.name = "Commercial Bank";
            this.imageUrl = "https://objects-qpon-qa.s3.ap-southeast-1.amazonaws.com/fid-935e9f19-7460-41a1-94fa-394e7028958f.png";
            this.approvalStatus = ApprovalStatus.APPROVED;
            this.isActive = true;
        } else {
            this.id = "uid-16d702b7-03c2-4aa7-872b-14f80c0a439a";
            this.name = "People's Bank";
            this.imageUrl = "https://objects-qpon-qa.s3.ap-southeast-1.amazonaws.com/fid-553869d3-df54-46b3-8590-a3d93a5b16c0.png";
            this.approvalStatus = ApprovalStatus.APPROVED;
            this.isActive = true;
        }
    }

    private void setMerchant(int i) {
        if (i == 0) {
            this.id = "uid-49e6e134-df02-4fb9-8c80-31fe82f5beb9";
            this.name = "ODEL";
            this.imageUrl = "https://objects-qpon-qa.s3.ap-southeast-1.amazonaws.com/fid-494a1345-0d3c-4236-9c2f-e1cceb55feaf.png";
            this.approvalStatus = ApprovalStatus.APPROVED;
            this.isActive = true;
        } else {
            this.id = "uid-6c9922fa-f4e0-4664-b8be-b4ed61dda61d";
            this.name = "Keells";
            this.imageUrl = "https://objects-qpon-qa.s3.ap-southeast-1.amazonaws.com/fid-1594789c-ed37-4369-b1b8-82e0c49e90d1.png";
            this.approvalStatus = ApprovalStatus.APPROVED;
            this.isActive = true;
        }
    }
}
