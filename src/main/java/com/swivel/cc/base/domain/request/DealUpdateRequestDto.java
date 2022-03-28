package com.swivel.cc.base.domain.request;

import com.swivel.cc.base.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DealUpdateRequestDto extends DealRequestDto {
    private String id;

    @Override
    public boolean isRequiredAvailable(UserType userType) {
        return isNonEmpty(id) && super.isRequiredAvailable(userType);
    }
}
