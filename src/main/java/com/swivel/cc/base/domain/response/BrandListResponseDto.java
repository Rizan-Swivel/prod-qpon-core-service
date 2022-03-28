package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Brand;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Response entity for brand list
 */
@Setter
@Getter
public class BrandListResponseDto extends PageResponseDto {

    private final List<BrandResponseDto> brands;

    public BrandListResponseDto(Page<Brand> brandPage, String timeZone) {
        super(brandPage);
        this.brands = convertToBrandListResponseDto(brandPage, timeZone);
    }

    /**
     * Convert Brand page into Brand Response Dto List.
     *
     * @param brandPage brand page
     * @return Brand Response Dto List
     */
    private List<BrandResponseDto> convertToBrandListResponseDto(Page<Brand> brandPage, String timeZone) {
        List<BrandResponseDto> brandList = new ArrayList<>();
        for (var i = 0; i < brandPage.getContent().size(); i++) {
            var brandResponseDto = new BrandResponseDto(brandPage.getContent().get(i), timeZone);
            brandList.add(brandResponseDto);
        }
        return brandList;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
