package com.swivel.cc.base.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Category view count response Dto
 */
@Getter
@Setter
@AllArgsConstructor
public class CategoryViewCountResponseDto {

    private CategoryResponseDto category;
    private long viewCount;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String displayDate;
}
