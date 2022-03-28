package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.AuthUser;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Merchant response Dto
 */

@Setter
@Getter
@NoArgsConstructor
public class MerchantBusinessResponseDto extends BasicMerchantBusinessResponseDto {

    private DateResponseDto joinedOn;

    public MerchantBusinessResponseDto(DateResponseDto joinedOn, String id, AuthUser authUser) {
        super(id, authUser);
        this.joinedOn = joinedOn;
    }

    public MerchantBusinessResponseDto(String merchantId, AuthUser authUser) {
        super(merchantId, authUser);
    }
}