package com.swivel.cc.base.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CreditCardRequestUpdateRequestDto extends CreditCardRequestCreateRequestDto {

    private String id;

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(id) && super.isRequiredAvailable();
    }
}
