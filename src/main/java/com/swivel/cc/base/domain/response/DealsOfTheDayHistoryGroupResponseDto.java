package com.swivel.cc.base.domain.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Deals of the day history group response Dto
 */
@Getter
public class DealsOfTheDayHistoryGroupResponseDto extends PageResponseDto {

    private final List<DealsOfTheDayHistoryResponseDto> dealGroups = new ArrayList<>();


    public DealsOfTheDayHistoryGroupResponseDto(Page<DealsOfTheDayHistoryResponseDto> page,
                                                DealsOfTheDayHistoryResponseDto responseDto) {
        super(page);
        dealGroups.add(responseDto);
    }
}
