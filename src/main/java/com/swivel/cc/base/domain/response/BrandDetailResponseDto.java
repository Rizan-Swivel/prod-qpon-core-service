package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BrandDetailResponseDto extends BrandResponseDto {

    private final long noOfActiveMerchants;
    private final long noOfActiveDeals;
    private long noOfMerchants;
    private long noOfDeals;
    private long noOfBanks;
    private long noOfActiveBanks;

    public BrandDetailResponseDto(Brand brand, long noOfMerchants,
                                  long noOfDeals, String timeZone, long noOfActiveDeals, long noOfActiveMerchants) {
        super(brand, timeZone);
        this.noOfActiveMerchants = noOfActiveMerchants;
        this.noOfActiveDeals = noOfActiveDeals;
        this.noOfDeals = noOfDeals;
        this.noOfMerchants = noOfMerchants;
    }
}
