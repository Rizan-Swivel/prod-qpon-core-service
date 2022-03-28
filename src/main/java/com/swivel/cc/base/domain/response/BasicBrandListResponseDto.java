package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

@Setter
@Getter
public class BasicBrandListResponseDto extends PageResponseDto {

    private final List<BasicBrandResponseDto> brands;

    public BasicBrandListResponseDto(Page<Brand> brandPage) {
        super(brandPage);
        this.brands = convertToBrandListResponseDto(brandPage);
    }

    /**
     * Convert Brand page into Basic Brand Response Dto List.
     *
     * @param brandPage brand page
     * @return Basic Brand Response Dto List
     */
    private List<BasicBrandResponseDto> convertToBrandListResponseDto(Page<Brand> brandPage) {
        List<BasicBrandResponseDto> brandList = new ArrayList<>();
        for (var i = 0; i < brandPage.getContent().size(); i++) {
            var brandResponseDto = new BasicBrandResponseDto(brandPage.getContent().get(i));
            brandList.add(brandResponseDto);
        }
        return brandList;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
