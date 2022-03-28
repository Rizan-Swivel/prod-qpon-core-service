package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class DealViewsWithDateResponseDto extends DealViewsResponseDto {

    private String displayDate;

    public DealViewsWithDateResponseDto(Deal deal, MerchantBusinessResponseDto merchant,
                                        long viewCount, String displayDate) {
        super(deal, merchant, viewCount);
        this.displayDate = displayDate;
    }
}
