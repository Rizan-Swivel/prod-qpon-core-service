package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

/**
 * Base class for paging dto
 */
@Setter
@Getter
public class PageResponseDto extends ResponseDto {

    private final long totalItems;
    private final int totalPages;
    private final int page;
    private final int size;

    public PageResponseDto(Page<?> page) {
        this.totalItems = page.getTotalElements();
        this.totalPages = page.getTotalPages();
        this.page = page.getNumber();
        this.size = page.getSize();
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
