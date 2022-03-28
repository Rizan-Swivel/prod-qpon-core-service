package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.RequestADeal;
import com.swivel.cc.base.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

/**
 * Detail request deal response dto
 */
@Getter
@Setter
public class DetailRequestADealResponseDto extends ResponseDto {

    private String id;
    private BasicCategoryResponseDto category;
    private BasicBrandResponseDto brand;
    private BasicMerchantBusinessResponseDto merchant;
    private String note;
    private String products;
    private OfferTypeResponseDto offerType;
    private UserType toUserType;
    private DateResponseDto createdAt;

    public DetailRequestADealResponseDto(RequestADeal requestADeal,
                                         BasicMerchantBusinessResponseDto merchant,
                                         String timeZone) {
        this.id = requestADeal.getId();
        this.category = new BasicCategoryResponseDto(requestADeal.getCategory());
        this.brand = (requestADeal.getBrand() != null) ? new BasicBrandResponseDto(requestADeal.getBrand()) : null;
        this.merchant = merchant;
        this.note = requestADeal.getNote();
        this.products = requestADeal.getProducts();
        this.offerType = new OfferTypeResponseDto(requestADeal.getOfferType().getId(),
                requestADeal.getOfferType().getName());
        this.createdAt = new DateResponseDto(requestADeal.getUpdatedAt(),
                timeZone,
                new Date(requestADeal.getUpdatedAt()));
        this.toUserType = requestADeal.getToUserType();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
