package com.swivel.cc.base.enums;

import lombok.Getter;

/**
 * Error response status type
 */
@Getter
public enum ErrorResponseStatusType {

    INTERNAL_SERVER_ERROR(5000, "Internal server error."),
    MISSING_REQUIRED_FIELDS(4000, "Required fields are missing."),
    INVALID_EXPIRY_DATE(4001, "Invalid expiry date."),
    INVALID_CATEGORY_ID(4002, "Invalid category id."),
    INVALID_BRAND_ID(4003, "Invalid brand id."),
    INVALID_VALID_FROM_DATE(4004, "Invalid validFrom date."),
    INVALID_EXPIRED_ON_DATE(4005, "Invalid expiredOn date."),
    INVALID_DEAL_ID(4006, "Invalid deal id."),
    INVALID_TIMEZONE(4100, "Invalid time zone."),
    INVALID_MERCHANT_ID(4101, "Invalid merchant Id."),
    INVALID_PRICE(4007, "Invalid price/ amount."),
    INVALID_REQUEST_DEAL_ID(4008, "Invalid request a deal Id."),
    INVALID_OFFER_TYPE_ID(4009, "Invalid offerType id."),
    INVALID_DATE_OPTION(4010, "Invalid date option."),
    INVALID_DATE_RANGE(4011, "Start date should be less than or equal to end date."),
    UNSUPPORTED_OPTION_FOR_DATE_RANGE(4012, "Unsupported option for date range."),
    INVALID_NOTIFICATION_TEMPLATE_TYPE(4013, "Invalid notification template type."),
    INACTIVE_MERCHANT_FOR_CREATE_DEAL(4014, "Invalid/ Inactive merchant cannot create any deals."),
    UNSUPPORTED_DEAL_UPDATE(4015, "Approved Deals cannot be updated."),
    EXISTING_CATEGORY_NAME(4016, "Existing Category Name."),
    EXISTING_BRAND_NAME(4017, "Existing Brand Name."),
    UNSUPPORTED_BRAND_DELETE(4018, "The band can not be deleted because it is already mapped with merchants."),
    UNSUPPORTED_CATEGORY_DELETE(4019, "The category can not be deleted because it is already mapped with merchants or another category."),
    INVALID_CATEGORY_FOR_RELATED_CATEGORIES(4020, "Normal category is not allowed to have related categories."),
    INVALID_USER_ROLE(4021, "Invalid user role."),
    INVALID_CREDIT_CARD_REQUEST_ID(4022, "Invalid Credit card request id."),
    INVALID_BANK_ID(4023, "Invalid bank id."),
    INVALID_MOBILE_NUMBER(4024, "Invalid Mobile Number."),
    INVALID_EMAIL(4025, "Invalid Email."),
    INVALID_NIC(4026, "Invalid NIC."),
    INVALID_USER_OR_BANK(4027, "Invalid Bank or User."),
    UNSUPPORTED_ROLE_TYPE_ACTION(4028, "This user type cannot perform this action."),
    INACTIVE_MERCHANT(4029, "Inactive merchant."),
    BUSINESS_PROFILE_NOT_FOUND(4030, "Approved business profile not found."),
    INVALID_USER_ID_OR_TYPE(4031, "Invalid userId or userType."),
    UNSUPPORTED_USER_TYPE_ACTION(4032, "Unsupported user type for the action."),
    INVALID_DEDUCTION_AMOUNT(4033, "Invalid deduction amount."),
    INVALID_DEDUCTION_PERCENTAGE(4034, "Invalid deduction percentage.");

    private final int code;
    private final String message;

    ErrorResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Error code covert into string to read display message from error property file
     *
     * @param errorCode errorCode
     * @return errorCode as string
     */
    public static String getCodeString(int errorCode) {
        return Integer.toString(errorCode);
    }

}
