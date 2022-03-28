package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.RequestADeal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Basic request deal list response dto
 */
@Getter
@Setter
public class BasicRequestADealListResponseDto extends PageResponseDto {

    private final List<BasicRequestADealResponseDto> dealRequests = new ArrayList<>();

    public BasicRequestADealListResponseDto(Page<RequestADeal> page,
                                            Map<String, MerchantBusinessResponseDto> merchantResponseDtoMap,
                                            String timeZone) {
        super(page);
        page.getContent().forEach(requestADeal -> dealRequests.add
                (new BasicRequestADealResponseDto(requestADeal,
                        merchantResponseDtoMap.get(requestADeal.getMerchantId()), timeZone)));
    }
}
