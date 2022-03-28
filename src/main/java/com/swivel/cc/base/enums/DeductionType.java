package com.swivel.cc.base.enums;

public enum DeductionType {
    PERCENTAGE("PERCENTAGE"),
    AMOUNT("AMOUNT");

    private final String type;

    DeductionType(String type) {
        this.type = type;
    }
}