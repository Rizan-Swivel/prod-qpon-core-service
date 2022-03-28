package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Category;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BasicCategoryListResponseDto extends PageResponseDto {

    private final List<BasicCategoryResponseDto> categories;

    public BasicCategoryListResponseDto(Page<Category> categoryPage) {
        super(categoryPage);
        this.categories = convertToCategoryListResponseDto(categoryPage);
    }

    /**
     * Convert Category page into Category Response Dto List.
     *
     * @param categoryPage categoryPage
     * @return Category Response Dto List
     */
    private List<BasicCategoryResponseDto> convertToCategoryListResponseDto(Page<Category> categoryPage) {
        List<BasicCategoryResponseDto> categoryList = new ArrayList<>();
        for (var i = 0; i < categoryPage.getContent().size(); i++) {
            var categoryResponseDto = new BasicCategoryResponseDto(categoryPage.getContent().get(i));
            categoryList.add(categoryResponseDto);
        }
        return categoryList;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }

}
