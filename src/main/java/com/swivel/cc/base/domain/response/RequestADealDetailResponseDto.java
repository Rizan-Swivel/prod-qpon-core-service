package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.RequestADeal;
import com.swivel.cc.base.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class RequestADealDetailResponseDto extends ResponseDto {

    private String note;
    private String products;
    private OfferTypeResponseDto offerType;
    private BasicUserResponseDto user;
    private DateResponseDto requestedOn;
    private BasicBrandResponseDto brand;
    private BasicCategoryResponseDto category;
    private BasicMerchantBusinessResponseDto merchant;
    private UserType toUserType;

    public RequestADealDetailResponseDto(RequestADeal requestADeal, Category category, Brand brand,
                                         BusinessMerchantResponseDto merchant,
                                         BasicUserResponseDto basicUserResponseDto, String timeZone) {
        this.note = requestADeal.getNote();
        this.products = requestADeal.getProducts();
        this.offerType = ((requestADeal.getOfferType() != null)
                ? new OfferTypeResponseDto(requestADeal.getOfferType().getId(), requestADeal.getOfferType().getName())
                : null);
        this.user = basicUserResponseDto;
        this.toUserType = requestADeal.getToUserType();
        this.requestedOn = new DateResponseDto(
                requestADeal.getCreatedAt(), timeZone, new Date(requestADeal.getCreatedAt()));
        this.brand = ((brand != null) ? new BasicBrandResponseDto(brand.getId(), brand.getName()) : null);
        this.category = new BasicCategoryResponseDto(category.getId(), category.getName());
        this.merchant = new BasicMerchantBusinessResponseDto(merchant);
    }
}
