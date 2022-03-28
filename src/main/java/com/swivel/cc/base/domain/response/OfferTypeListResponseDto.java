package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.OfferType;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
public class OfferTypeListResponseDto extends ResponseDto {
    private List<OfferTypeResponseDto> offerTypes = new ArrayList<>();


    public OfferTypeListResponseDto(List<OfferType> offerTypesList) {

        for (var offerType : offerTypesList) {
            var offerTypeResponseDto = new OfferTypeResponseDto(offerType.getId(), offerType.getName());
            offerTypes.add(offerTypeResponseDto);
        }
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
