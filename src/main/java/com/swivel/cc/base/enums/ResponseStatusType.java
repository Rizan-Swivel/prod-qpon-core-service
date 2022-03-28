package com.swivel.cc.base.enums;

/**
 * Enum values for response
 */
public enum ResponseStatusType {

    SUCCESS("SUCCESS"),
    ERROR("ERROR");

    private final String status;

    ResponseStatusType(String status) {
        this.status = status;
    }

}
