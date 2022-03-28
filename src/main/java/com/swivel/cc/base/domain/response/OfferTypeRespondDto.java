package com.swivel.cc.base.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class OfferTypeRespondDto extends ResponseDto {

    private String id;
    private String name;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
