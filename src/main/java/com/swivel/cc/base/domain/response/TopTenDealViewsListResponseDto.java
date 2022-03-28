package com.swivel.cc.base.domain.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deal view list response dto
 */
@Getter
public class TopTenDealViewsListResponseDto extends PageResponseDto {

    private final List<DealViewsResponseDto> views = new ArrayList<>();

    public TopTenDealViewsListResponseDto(Page<DealViewCountResponse> page,
                                          Map<String, MerchantBusinessResponseDto> merchantBusinessResponseDtoMap) {
        super(page);
        for (DealViewCountResponse deals : page.getContent()) {
            views.add(new DealViewsResponseDto(deals.getDeal(),
                    merchantBusinessResponseDtoMap.get(deals.getDeal().getMerchantId()), deals.getViewCount()));
        }
    }
}
