package com.swivel.cc.base.enums;

import lombok.Getter;

@Getter
public enum ApprovalStatus {
    PENDING("PENDING"),
    APPROVED("APPROVED"),
    REJECTED("REJECTED"),
    UNBLOCKED("UNBLOCKED"),
    BLOCKED("BLOCKED");

    private final String status;

    ApprovalStatus(String status) {
        this.status = status;
    }
}
