package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BasicCategoryResponseDto extends ResponseDto {

    private String id;
    private String name;
    private String imageUrl;

    public BasicCategoryResponseDto(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        this.imageUrl = category.getImageUrl();
    }

    public BasicCategoryResponseDto(String categoryId, String name) {
        this.id = categoryId;
        this.name = name;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
