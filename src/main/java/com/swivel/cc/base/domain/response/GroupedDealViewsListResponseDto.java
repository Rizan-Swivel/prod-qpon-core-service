package com.swivel.cc.base.domain.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
public class GroupedDealViewsListResponseDto extends PageResponseDto {

    private final List<GroupedDealViewsResponseDto> views = new ArrayList<>();

    public GroupedDealViewsListResponseDto(Page<DealViewCountResponse> page,
                                           Map<String, MerchantBusinessResponseDto> merchantBusinessResponseDtoMap) {
        super(page);
        for (DealViewCountResponse deals : page.getContent()) {
            views.add(new GroupedDealViewsResponseDto(deals.getDeal(),
                    merchantBusinessResponseDtoMap.get(deals.getDeal().getMerchantId()), deals.getViewCount()));
        }
    }
}
