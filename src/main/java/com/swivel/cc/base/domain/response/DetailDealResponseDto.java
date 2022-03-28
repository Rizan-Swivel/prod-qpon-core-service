package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import lombok.Getter;
import lombok.Setter;

/**
 * Detail deal response Dto
 */
@Setter
@Getter
public class DetailDealResponseDto extends DealResponseDto {
    private NumberOfViewsResponseDto views;

    public DetailDealResponseDto(Deal deal, String timeZone,
                                 BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto) {
        super(deal, timeZone, basicMerchantBusinessResponseDto);
    }

    public DetailDealResponseDto(Deal deal, String timeZone,
                                 BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto,
                                 BasicMerchantBusinessResponseDto basicBankBusinessResponseDto) {
        super(deal, timeZone, basicMerchantBusinessResponseDto, basicBankBusinessResponseDto);
    }
}
