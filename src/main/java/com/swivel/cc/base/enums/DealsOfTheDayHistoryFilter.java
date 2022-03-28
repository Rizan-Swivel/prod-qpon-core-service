package com.swivel.cc.base.enums;

import com.swivel.cc.base.exception.InvalidDateOptionException;

/**
 * Deals of the day history filter
 */
public enum DealsOfTheDayHistoryFilter {

    TODAY("TODAY"),
    YESTERDAY("YESTERDAY"),
    LAST_WEEK("LAST-WEEK");

    private static final String INVALID_DATE_OPTION_TYPE = "Invalid date filter.";
    private final String option;

    DealsOfTheDayHistoryFilter(String option) {
        this.option = option;
    }

    /**
     * This method returns relevant date option.
     *
     * @param type type
     * @return date option
     */
    public static DealsOfTheDayHistoryFilter getOption(String type) {
        if (type != null) {
            for (DealsOfTheDayHistoryFilter dealsOfTheDayHistoryFilter : DealsOfTheDayHistoryFilter.values()) {
                if (dealsOfTheDayHistoryFilter.option.equalsIgnoreCase(type.trim())) {
                    return dealsOfTheDayHistoryFilter;
                }
            }
        }
        throw new InvalidDateOptionException(INVALID_DATE_OPTION_TYPE);
    }
}
