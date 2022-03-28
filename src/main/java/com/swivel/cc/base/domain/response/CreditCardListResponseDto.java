package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CreditCardRequest;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
public class CreditCardListResponseDto extends PageResponseDto {

    List<CreditCardListDetailResponseDto> creditCardRequests = new ArrayList<>();

    public CreditCardListResponseDto(Page<CreditCardRequest> creditCardRequestsPage, String timeZone) {
        super(creditCardRequestsPage);
        creditCardRequestsPage.getContent().forEach(creditCardRequest -> {
            var detailCreditCardRequestResponseDto = new CreditCardListDetailResponseDto(creditCardRequest, timeZone);
            creditCardRequests.add(detailCreditCardRequestResponseDto);
        });
    }
}
