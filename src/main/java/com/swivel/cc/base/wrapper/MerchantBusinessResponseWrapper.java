package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class MerchantBusinessResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private BusinessMerchantResponseDto data;
    private String displayMessage;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
