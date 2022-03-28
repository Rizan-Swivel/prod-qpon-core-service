package com.swivel.cc.base.domain.request;


import lombok.Getter;
import lombok.Setter;

/**
 * Request a deal request dto
 */
@Getter
@Setter
public class RequestADealCreateRequestDto extends RequestDto {

    private String userId;
    private String merchantId;
    private String categoryId;
    private String note;
    private String brandId;
    private String products;
    private String offerTypeId;

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(merchantId) && isNonEmpty(categoryId) && isNonEmpty(note) && isNonEmpty(userId);
    }


    @Override
    public String toLogJson() {
        return toJson();
    }
}
