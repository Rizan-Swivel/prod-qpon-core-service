package com.swivel.cc.base.domain.response;


import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SingleRequestADealSummaryResponseDto extends ResponseDto {

    private BasicMerchantBusinessResponseDto merchant;
    private BasicCategoryResponseDto category;
    private BasicBrandResponseDto brand;
    private long noOfRequests;


    public SingleRequestADealSummaryResponseDto(BasicMerchantBusinessResponseDto merchant,
                                                Category category, Brand brand, long count) {
        this.merchant = merchant;
        this.category = new BasicCategoryResponseDto(category.getId(), category.getName());
        this.brand = (brand != null) ? new BasicBrandResponseDto(brand.getId(), brand.getName()) : null;
        this.noOfRequests = count;
    }
}
