package com.swivel.cc.base.domain.response;


import com.swivel.cc.base.domain.entity.RequestADeal;
import com.swivel.cc.base.enums.UserType;
import lombok.Getter;
import lombok.Setter;

/**
 * Response dto for request a deal
 */
@Getter
@Setter
public class RequestADealCreateResponseDto extends ResponseDto {

    private String id;
    private String userId;
    private String merchantId;
    private String categoryId;
    private String note;
    private String brandId;
    private String products;
    private String offerTypeId;
    private UserType toUserType;

    public RequestADealCreateResponseDto(RequestADeal requestADeal) {
        this.id = requestADeal.getId();
        this.userId = requestADeal.getUserId();
        this.merchantId = requestADeal.getMerchantId();
        this.categoryId = requestADeal.getCategory().getId();
        this.note = requestADeal.getNote();
        this.products = requestADeal.getProducts();
        if (requestADeal.getBrand() != null) {
            this.brandId = requestADeal.getBrand().getId();
        } else {
            this.brandId = null;
        }
        if (requestADeal.getOfferType() != null) {
            this.offerTypeId = requestADeal.getOfferType().getId();
        } else {
            this.offerTypeId = null;
        }
        this.toUserType = requestADeal.getToUserType();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
