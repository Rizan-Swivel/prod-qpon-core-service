package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class GroupedDealViewsResponseDto extends ResponseDto {

    private DealReportResponseDto deal;
    private MerchantBusinessResponseDto merchant;
    private long totalViewCount;

    public GroupedDealViewsResponseDto(Deal deal, MerchantBusinessResponseDto merchant, long viewCount) {
        this.deal = new DealReportResponseDto(deal);
        this.merchant = merchant;
        this.totalViewCount = viewCount;
    }
}
