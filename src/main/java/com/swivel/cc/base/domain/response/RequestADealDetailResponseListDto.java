package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.RequestADeal;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class RequestADealDetailResponseListDto extends PageResponseDto {

    List<RequestADealDetailResponseDto> requestADeals;

    public RequestADealDetailResponseListDto(Page<RequestADeal> requestADeals, Map<String,
            BasicUserResponseDto> userResponseDtoMap, Category category, Brand brand,
                                             BusinessMerchantResponseDto merchant, String timeZone) {
        super(requestADeals);
        List<RequestADealDetailResponseDto> dealDetails = new ArrayList<>();


        for (var requestADeal : requestADeals) {
            var requestADealDetailResponse = new RequestADealDetailResponseDto(requestADeal, category,
                    brand, merchant, userResponseDtoMap.get(requestADeal.getUserId()), timeZone);
            dealDetails.add(requestADealDetailResponse);
        }

        this.requestADeals = dealDetails;
    }
}
