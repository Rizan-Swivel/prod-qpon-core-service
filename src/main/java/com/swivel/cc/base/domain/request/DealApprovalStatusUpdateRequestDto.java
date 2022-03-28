package com.swivel.cc.base.domain.request;

import com.swivel.cc.base.enums.ApprovalStatus;
import lombok.Getter;
import lombok.Setter;

/**
 * Deal status update request dto
 */

@Setter
@Getter
public class DealApprovalStatusUpdateRequestDto extends RequestDto {

    private String id;
    private ApprovalStatus approvalStatus;
    private String comment;

    @Override
    public boolean isRequiredAvailable() {

        return isNonEmpty(id) && approvalStatus != null && (approvalStatus.equals(ApprovalStatus.APPROVED) ||
                (approvalStatus.equals(ApprovalStatus.REJECTED) && isNonEmpty(comment)));
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
