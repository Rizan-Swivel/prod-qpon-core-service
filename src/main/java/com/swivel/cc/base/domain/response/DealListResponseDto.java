package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.entity.DealSearchIndex;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Deal list response dto
 */
@Getter
@Setter
public class DealListResponseDto extends PageResponseDto {

    private final List<BasicDealResponseDto> deals;

    public DealListResponseDto(Page<Deal> dealPage, Map<String, MerchantBusinessResponseDto> merchantResponseDtoMap,
                               String timeZone) {
        super(dealPage);
        this.deals = new ArrayList<>();
        for (var i = 0; i < dealPage.getContent().size(); i++) {
            var basicDealResponseDto = new BasicDealResponseDto(dealPage.getContent().get(i),
                    merchantResponseDtoMap.get(dealPage.getContent().get(i).getMerchantId()), timeZone);
            deals.add(basicDealResponseDto);
        }
    }

    public DealListResponseDto(Page<Deal> dealPage, BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto,
                               String timeZone) {
        super(dealPage);
        this.deals = new ArrayList<>();
        for (var i = 0; i < dealPage.getContent().size(); i++) {
            var basicDealResponseDto = new BasicDealResponseDto(dealPage.getContent().get(i),
                    basicMerchantBusinessResponseDto, timeZone);
            deals.add(basicDealResponseDto);
        }
    }

    public DealListResponseDto(Page<DealSearchIndex> dealPage, String timeZone) {
        super(dealPage);
        this.deals = new ArrayList<>();
        dealPage.getContent().forEach(dealSearchIndex -> {
            var basicDealResponseDto = new BasicDealResponseDto(dealSearchIndex, timeZone);
            deals.add(basicDealResponseDto);
        });
    }

    public DealListResponseDto(BasicMerchantBusinessResponseDto merchantBusinessResponseDto,
                               BasicMerchantBusinessResponseDto bankBusinessResponseDto,
                               Page<BankDeal> bankDealPage, String timeZone) {
        super(bankDealPage);
        this.deals = new ArrayList<>();
        for (var i = 0; i < bankDealPage.getContent().size(); i++) {
            var basicDealResponseDto = new BasicDealResponseDto(bankDealPage.getContent().get(i),
                    merchantBusinessResponseDto, bankBusinessResponseDto, timeZone);
            deals.add(basicDealResponseDto);
        }
    }

    @Override
    public String toLogJson() {
        return toJson();
    }

}
