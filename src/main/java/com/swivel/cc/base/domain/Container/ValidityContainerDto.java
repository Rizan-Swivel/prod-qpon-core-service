package com.swivel.cc.base.domain.Container;

import com.swivel.cc.base.enums.ErrorResponseStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class ValidityContainerDto {

    private boolean isValid;
    private ErrorResponseStatusType errorResponseStatusType;
}
