package com.swivel.cc.base.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import org.springframework.data.domain.Page;

import java.util.List;

@Getter
public class AllBasicBankResponseDto extends PageResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<BasicBankResponseDto> banks;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    List<BasicBankResponseDto> merchants;

    public AllBasicBankResponseDto(Page<BasicBankResponseDto> bankListPage, List<BasicBankResponseDto> bankList) {
        super(bankListPage);
        this.banks = bankList;
    }

    public AllBasicBankResponseDto(List<BasicBankResponseDto> bankList, Page<BasicBankResponseDto> bankListPage) {
        super(bankListPage);
        this.merchants = bankList;
    }
}
