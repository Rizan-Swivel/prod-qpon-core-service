package com.swivel.cc.base.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@AllArgsConstructor
public class NumberOfViewsResponseDto extends ResponseDto {
    private int count;
    private String displayValue;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
