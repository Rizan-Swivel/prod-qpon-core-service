package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CombinedDealRequest;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedDealRequestResponseDto extends ResponseDto {

    private BasicCategoryResponseDto category;
    private BasicBrandResponseDto brand;
    private MerchantBusinessResponseDto merchant;
    private long numberOfRequests;

    public CombinedDealRequestResponseDto(CombinedDealRequest combinedDealRequest, MerchantBusinessResponseDto merchant) {
        this.category = new BasicCategoryResponseDto(combinedDealRequest.getCategoryId(), combinedDealRequest.getCategoryName());
        this.brand = new BasicBrandResponseDto(combinedDealRequest.getBrandId(), combinedDealRequest.getBrandName());
        this.numberOfRequests = combinedDealRequest.getCount();
        this.merchant = merchant;
    }

}
