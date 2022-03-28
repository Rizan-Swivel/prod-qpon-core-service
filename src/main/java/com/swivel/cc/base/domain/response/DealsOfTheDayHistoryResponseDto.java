package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deals of the day history response Dto
 */
@Getter
@Setter
public class DealsOfTheDayHistoryResponseDto extends ResponseDto {

    private String displayDate;
    private List<BasicDealResponseDto> deals;

    public DealsOfTheDayHistoryResponseDto(long timeStamp, String timeZone, List<Deal> dealList,
                                           Map<String, MerchantBusinessResponseDto> merchantResponseDtoMap) {

        this.displayDate = new DateResponseDto(timeStamp, timeZone, new Date(timeStamp)).getDisplayDate();
        this.deals = new ArrayList<>();
        for (Deal deal : dealList) {
            var basicDealResponseDto = new BasicDealResponseDto(deal,
                    merchantResponseDtoMap.get(deal.getMerchantId()), timeZone);
            deals.add(basicDealResponseDto);
        }
    }
}
