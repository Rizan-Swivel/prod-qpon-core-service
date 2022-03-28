package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CombinedCreditCardRequestResponseDto extends ResponseDto {
    private MerchantBusinessResponseDto bank;
    private long numberOfRequests;

    public CombinedCreditCardRequestResponseDto(MerchantBusinessResponseDto bank, long numberOfRequests) {
        this.bank = bank;
        this.numberOfRequests = numberOfRequests;
    }
}
