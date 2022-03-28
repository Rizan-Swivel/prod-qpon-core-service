package com.swivel.cc.base.enums;


import lombok.Getter;

/**
 * Success Response Status Type
 */
@Getter
public enum SuccessResponseStatusType {

    UPDATE_USER(2000, "Successfully updated the approval status."),
    READ_BRAND_LIST(2001, "Successfully returned the brand list."),
    READ_CATEGORY_LIST(2002, "Successfully returned the category list."),
    CREATE_CATEGORY(2003, "Successfully created the category."),
    CREATE_BRAND(2004, "Successfully created the brand."),
    CREATE_DEAL(2005, "Successfully created the deal."),
    DELETE_DEAL(2006, "Successfully deleted the deal."),
    APPROVE_DEAL(2007, "Successfully updated the deal."),
    READ_DEAL_LIST(2008, "Successfully returned the deal list."),
    READ_DEAL(2009, "Successfully returned the deal."),
    READ_MERCHANT_LIST(2010, "Successfully returned the merchant list."),
    CREATE_CATEGORIES_BRANDS_FOR_MERCHANT(2011, "Successfully created mapping of categories and brands."),
    READ_CATEGORIES_BRANDS_FOR_MERCHANT(2012, "Successfully returned mapping of categories and brands."),
    CREATE_CATEGORIES_FOR_MERCHANT(2013, "Successfully created the categories for merchant."),
    READ_BRAND_DETAIL(2014, "Successfully returned the brand detail."),
    READ_CATEGORY_DETAIL(2015, "Successfully returned the category detail."),
    UPDATE_CATEGORIES_BRANDS_FOR_MERCHANT(2016, "Successfully updated mapping of categories and brands."),
    CREATE_REQUEST_A_DEAL(2017, "Successfully created the request a deal."),
    READ_REQUEST_DEAL_LIST(2018, "Successfully returned the request a deal list."),
    READ_REQUEST_A_DEAL(2019, "Successfully returned the request a deal groups list."),
    READ_REQUEST_DEAL_DETAIL(2020, "Successfully returned the request a deal detail."),
    CREATE_OFFER_TYPE(2021, "Successfully created the offer type."),
    READ_OFFER_TYPE_LIST(2022, "Successfully returned the offer type list."),
    UPDATE_OFFER_TYPE(2023, "Successfully updated the offer type."),
    UPDATE_CATEGORY(2024, "Successfully updated the category."),
    UPDATE_BRAND(2025, "Successfully updated the brand."),
    READ_CATEGORIES_FOR_MERCHANT(2026, "Successfully returned the category list for merchant."),
    READ_BRAND_FOR_MERCHANT(2027, "Successfully returned the brand list for merchant."),
    REQUEST_A_DEAL_COMBINATION_SUMMARY(2028, "Successfully returned the summary of the combination."),
    READ_DEAL_VIEW_GRAPH(2029, "Successfully returned top 10 list of deal's views."),
    READ_DEAL_VIEW_LIST(2030, "Successfully returned deal's views list."),
    UPDATE_DEAL(2031, "Successfully updated the deal."),
    DELETE_BRAND(2032, "Successfully deleted the brand."),
    CATEGORY_DELETE_SUCCESSFUL(2033, "Successfully deleted the category."),
    READ_RELATED_CATEGORIES(2034, "Successfully returned the related category list."),
    SUCCESSFULLY_RETURNED_SUMMARY(2035, "Successfully returned the summary."),
    CREATE_CREDIT_CARD_REQUEST(2036, "Successfully created the credit card request."),
    READ_CREDIT_CARD_REQUEST(2037, "Successfully returned the credit card request."),
    READ_CREDIT_CARD_REQUEST_LIST(2038, "Successfully returned the credit card request list."),
    DELETE_CREDIT_CARD_REQUEST(2039, "Successfully deleted the credit card request."),
    UPDATE_CREDIT_CARD_REQUEST(2040, "Successfully updated the credit card request."),
    READ_REQUEST_A_CREDIT_CARD_GROUP(2041, "Successfully returned the request a credit card groups list."),
    CATEGORY_REACH_LIST(2042, "Successfully returned category views list."),
    BANK_LIST(2043, "Successfully returned the banks list.");

    private final int code;
    private final String message;

    SuccessResponseStatusType(int code, String message) {
        this.code = code;
        this.message = message;
    }

    /**
     * Success code covert into string to read display message from success property file
     *
     * @param successCode successCode
     * @return string code
     */
    public String getCodeString(int successCode) {
        return Integer.toString(successCode);
    }
}
