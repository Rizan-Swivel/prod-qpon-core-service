package com.swivel.cc.base.domain.response;

import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Category audience reach response Dto
 */
@Getter
public class CategoryAudienceReachResponseDto extends PageResponseDto {

    private final List<CategoryViewCountResponseDto> views;

    public CategoryAudienceReachResponseDto(Page<CategoryViewCountResponseDto> page,
                                            List<CategoryViewCountResponseDto> categoryViews) {
        super(page);
        this.views = categoryViews;
    }
}
