package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CreditCardRequest;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Getter
@Setter
public class CreditCardListDetailResponseDto extends ResponseDto {

    private final String id;
    private final String userId;
    private final String bankId;
    private final String fullName;
    private final String city;
    private final DateResponseDto requestedOn;


    public CreditCardListDetailResponseDto(CreditCardRequest creditCardRequest, String timeZone) {
        this.id = creditCardRequest.getId();
        this.userId = creditCardRequest.getUserId();
        this.bankId = creditCardRequest.getBankId();
        this.fullName = creditCardRequest.getFullName();
        this.city = creditCardRequest.getCity();
        this.requestedOn = new DateResponseDto(creditCardRequest.getCreatedAt(), timeZone,
                new Date(creditCardRequest.getCreatedAt()));
    }
}
