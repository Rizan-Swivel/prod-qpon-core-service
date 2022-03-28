package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deal views list response Dto
 */
@Getter
@Setter
public class DealViewsListResponseDto extends PageResponseDto {

    private List<DealViewsWithDateResponseDto> views = new ArrayList<>();

    public DealViewsListResponseDto(Page<DealViewCountResponse> page,
                                    Map<String, MerchantBusinessResponseDto> merchantBusinessResponseDtoMap) {
        super(page);
        for (DealViewCountResponse deals : page.getContent()) {
            views.add(new DealViewsWithDateResponseDto(deals.getDeal(),
                    merchantBusinessResponseDtoMap.get(deals.getDeal().getMerchantId()),
                    deals.getViewCount(), deals.getDisplayDate()));
        }
    }
}
