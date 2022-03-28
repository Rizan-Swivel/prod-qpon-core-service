package com.swivel.cc.base.wrapper;

import com.swivel.cc.base.domain.BaseDto;
import com.swivel.cc.base.domain.response.BulkMerchantBusinessResponseDto;
import com.swivel.cc.base.enums.ResponseStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BulkMerchantResponseWrapper implements BaseDto {

    private ResponseStatusType status;
    private String message;
    private BulkMerchantBusinessResponseDto data;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
