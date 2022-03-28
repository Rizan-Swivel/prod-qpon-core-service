package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CombinedDealRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Setter
@Getter
public class ListCombinedDealRequestResponseDto extends PageResponseDto {


    private final List<CombinedDealRequestResponseDto> combinedDealRequestList;

    public ListCombinedDealRequestResponseDto(Page<CombinedDealRequest> dealRequestsPage, Map<String,
            MerchantBusinessResponseDto> merchantResponseDtoMap) {
        super(dealRequestsPage);
        this.combinedDealRequestList = convertToCombinedDealRequestList(dealRequestsPage.getContent(),
                merchantResponseDtoMap);
    }

    /**
     * This method convertCombinedDealRequest List into CombinedDealRequestResponseDto List
     *
     * @param dealRequestsList       List of CombinedDealRequest
     * @param merchantResponseDtoMap merchantResponseDtoMap
     * @return list of CombinedDealRequestResponseDto
     */
    private List<CombinedDealRequestResponseDto> convertToCombinedDealRequestList(
            List<CombinedDealRequest> dealRequestsList,
            Map<String, MerchantBusinessResponseDto> merchantResponseDtoMap) {

        List<CombinedDealRequestResponseDto> dealRequestList = new ArrayList<>();

        for (CombinedDealRequest combinedDealRequest : dealRequestsList) {
            var combinedDeal = new CombinedDealRequestResponseDto(combinedDealRequest, merchantResponseDtoMap.get(combinedDealRequest.getMerchantId()));
            dealRequestList.add(combinedDeal);
        }
        return dealRequestList;
    }
}
