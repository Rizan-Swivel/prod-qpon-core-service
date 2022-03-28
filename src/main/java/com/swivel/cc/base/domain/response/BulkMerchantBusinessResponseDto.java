package com.swivel.cc.base.domain.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Bulk user response dto to get list of users
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class BulkMerchantBusinessResponseDto extends ResponseDto {

    private List<MerchantBusinessResponseDto> merchants;

    @Override
    public String toLogJson() {
        return toJson();
    }
}
