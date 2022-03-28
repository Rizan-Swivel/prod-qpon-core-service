package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Category;
import lombok.Getter;
import lombok.Setter;

/**
 * Category detail response dto
 */
@Setter
@Getter
public class CategoryDetailResponseDto extends CategoryResponseDto {

    private long noOfMerchants;
    private long noOfDeals;
    private long noOfActiveMerchants;
    private long noOfActiveDeals;
    private long noOfBanks;
    private long noOfActiveBanks;

    public CategoryDetailResponseDto(Category category, long noOfDeals,
                                     long noOfMerchants, String timeZone, long noOfActiveMerchants, long noOfActiveDeals) {
        super(category, timeZone);
        this.noOfMerchants = noOfMerchants;
        this.noOfDeals = noOfDeals;
        this.noOfActiveDeals = noOfActiveDeals;
        this.noOfActiveMerchants = noOfActiveMerchants;
    }
}
