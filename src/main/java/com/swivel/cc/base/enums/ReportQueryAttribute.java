package com.swivel.cc.base.enums;

import lombok.Getter;

/**
 * Enum to contain google analytics report query attributes
 */
@Getter
public enum ReportQueryAttribute {
    EVENT_COUNT_METRIC("eventCount"),
    DEAL_ID_DIMENSION("customEvent:deal_id"),
    MERCHANT_ID_DIMENSION("customEvent:merchant_id"),
    CATEGORY_ID_DIMENSION("customEvent:category_id");

    private final String queryAttribute;

    ReportQueryAttribute(String queryAttribute) {
        this.queryAttribute = queryAttribute;
    }
}
