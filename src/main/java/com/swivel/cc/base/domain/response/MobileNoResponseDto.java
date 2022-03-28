package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Mobile number response dto
 */
@Setter
@Getter
@NoArgsConstructor
public class MobileNoResponseDto {
    private static final String SEPARATOR = "-";
    private static final String EMPTY = "";
    private static final int FIRST_PART_INDEX = 0;
    private static final int SECOND_PART_INDEX = 1;
    private String countryCode;
    private String localNumber;
    private String displayNumber;

    public MobileNoResponseDto(String mobileNo) {
        if (mobileNo != null && !mobileNo.trim().isEmpty() && mobileNo.contains(SEPARATOR)) {
            String[] mobileNoParts = mobileNo.split(SEPARATOR);
            this.countryCode = mobileNoParts[FIRST_PART_INDEX];
            this.localNumber = mobileNoParts[SECOND_PART_INDEX];
            this.displayNumber = mobileNo.trim().replace(SEPARATOR, EMPTY);
        }
    }
}
