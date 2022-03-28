package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.AuthUser;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchantIndex;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Merchant list response dto
 */
@Getter
@Setter
public class MerchantListResponseDto extends PageResponseDto {

    private List<MerchantBusinessResponseDto> merchants = new ArrayList<>();

    public MerchantListResponseDto(Page<?> page) {
        super(page);
        if (!page.getContent().isEmpty()) {
            if (page.getContent().get(0) instanceof CategoryBrandMerchantIndex) {
                List<CategoryBrandMerchantIndex> categoryBrandMerchantIndexList =
                        (List<CategoryBrandMerchantIndex>) page.getContent();
                this.merchants = categoryBrandMerchantListIntoMerchantResponseDto(categoryBrandMerchantIndexList);
            } else if (page.getContent().get(0) instanceof MerchantBusinessResponseDto) {
                this.merchants = (List<MerchantBusinessResponseDto>) page.getContent();
            }
        }
    }

    /**
     * This method returns converts a CategoryBrandMerchantIndex List into a MerchantBusinessResponseDto List.
     *
     * @param list CategoryBrandMerchantIndex List
     * @return List of MerchantBusinessResponseDto
     */

    private List<MerchantBusinessResponseDto> categoryBrandMerchantListIntoMerchantResponseDto(
            List<CategoryBrandMerchantIndex> list) {

        List<MerchantBusinessResponseDto> merchantBusinessResponseDtoList = new ArrayList<>();
        list.forEach(categoryBrandMerchantIndex ->
                merchantBusinessResponseDtoList.add(new MerchantBusinessResponseDto(categoryBrandMerchantIndex.getMerchantId(),
                                new AuthUser(
                                        categoryBrandMerchantIndex.getMerchantName(),
                                        categoryBrandMerchantIndex.getMerchantImageUrl())
                        )
                )
        );
        return merchantBusinessResponseDtoList;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
