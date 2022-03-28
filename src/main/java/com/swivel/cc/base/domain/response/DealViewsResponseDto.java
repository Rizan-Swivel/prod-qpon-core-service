package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import lombok.Getter;
import lombok.Setter;

/**
 * Deal views response Dto
 */
@Setter
@Getter
public class DealViewsResponseDto extends ResponseDto {

    private DealReportResponseDto deal;
    private MerchantBusinessResponseDto merchant;
    private long viewCount;

    public DealViewsResponseDto(Deal deal, MerchantBusinessResponseDto merchant, long viewCount) {
        this.deal = new DealReportResponseDto(deal);
        this.merchant = merchant;
        this.viewCount = viewCount;
    }
}
