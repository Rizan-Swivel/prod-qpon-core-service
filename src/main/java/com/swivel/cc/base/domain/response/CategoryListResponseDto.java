package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class CategoryListResponseDto extends PageResponseDto {

    private final List<CategoryResponseDto> categories;

    public CategoryListResponseDto(Page<Category> categoryPage, String timeZone) {
        super(categoryPage);
        this.categories = convertToCategoryListResponseDto(categoryPage, timeZone);
    }

    /**
     * Convert Category page into Category Response Dto List.
     *
     * @param categoryPage categoryPage
     * @return Category Response Dto List
     */
    private List<CategoryResponseDto> convertToCategoryListResponseDto(Page<Category> categoryPage,
                                                                       String timeZone) {
        List<CategoryResponseDto> categoryList = new ArrayList<>();
        for (var i = 0; i < categoryPage.getContent().size(); i++) {
            var categoryResponseDto = new CategoryResponseDto(categoryPage.getContent().get(i), timeZone);
            categoryList.add(categoryResponseDto);
        }
        return categoryList;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
