package com.swivel.cc.base.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class OfferTypeResponseDto extends ResponseDto {

    private String id;
    private String name;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
