package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CombinedCreditCardRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Getter
@Setter
public class ListCombinedCreditCardRequestResponseDto extends PageResponseDto {

    private List<CombinedCreditCardRequestResponseDto> combinedCreditCardRequestList;

    public ListCombinedCreditCardRequestResponseDto(
            Page<CombinedCreditCardRequest> creditCardRequestPage,
            Map<String, MerchantBusinessResponseDto> merchantResponseDtoMap) {
        super(creditCardRequestPage);
        this.combinedCreditCardRequestList =
                convertToCombinedDealRequestList(creditCardRequestPage.getContent(), merchantResponseDtoMap);
    }


    /**
     * convert combinedCreditCardRequest to combinedCreditCardRequestResponseDto.
     *
     * @param creditCardRequests     combinedCreditCardRequest list
     * @param merchantResponseDtoMap merchantResponseDtoMap
     * @return CombinedCreditCardRequestResponseDto list
     */
    private List<CombinedCreditCardRequestResponseDto> convertToCombinedDealRequestList(
            List<CombinedCreditCardRequest> creditCardRequests,
            Map<String, MerchantBusinessResponseDto> merchantResponseDtoMap
    ) {
        List<CombinedCreditCardRequestResponseDto> creditCardRequestList = new ArrayList<>();

        for (CombinedCreditCardRequest creditCardRequest : creditCardRequests) {
            var combinedCreditCardRequestResponseDto =
                    new CombinedCreditCardRequestResponseDto(merchantResponseDtoMap.get(creditCardRequest.getBankId()),
                            creditCardRequest.getCount());
            creditCardRequestList.add(combinedCreditCardRequestResponseDto);
        }
        return creditCardRequestList;
    }
}
