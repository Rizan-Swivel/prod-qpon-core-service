package com.swivel.cc.base.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.DeductionType;
import com.swivel.cc.base.enums.PrimaryType;
import com.swivel.cc.base.enums.SecondaryType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.HashSet;
import java.util.Set;

/**
 * Deal response dto
 */
@Setter
@Getter
public class DealResponseDto extends BasicDealResponseDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BasicMerchantBusinessResponseDto bank;
    private String description;
    private String dealCode;
    private String termsAndConditions;
    private PrimaryType primaryType;
    private SecondaryType secondaryType;
    private DeductionType deductionType;
    private Double deductionAmount;
    private Double deductionPercentage;
    private ApprovalStatus approvalStatus;
    private Set<BrandResponseDto> brands = new HashSet<>();
    private Set<CategoryResponseDto> categories = new HashSet<>();
    private DateResponseDto createdAt;
    private DateResponseDto updatedAt;

    public DealResponseDto(Deal deal, String timeZone, BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto) {
        super(deal, basicMerchantBusinessResponseDto, timeZone);
        this.description = deal.getDescription();
        this.dealCode = deal.getDealCode();
        this.termsAndConditions = deal.getTermsAndConditions();
        this.primaryType = deal.getPrimaryType();
        this.secondaryType = deal.getSecondaryType();
        this.deductionAmount = deal.getDeductionAmount();
        this.deductionPercentage = deal.getDeductionPercentage();
        this.approvalStatus = deal.getApprovalStatus();
        this.deductionType = deal.getDeductionType();
        deal.getRelatedCategories().forEach(category -> categories.add(new CategoryResponseDto(category, timeZone)));
        deal.getRelatedBrands().forEach(brand -> brands.add(new BrandResponseDto(brand, timeZone)));
        this.createdAt = new DateResponseDto(deal.getCreatedAt(), timeZone, new Date(deal.getCreatedAt()));
        this.updatedAt = new DateResponseDto(deal.getUpdatedAt(), timeZone, new Date(deal.getUpdatedAt()));
    }

    public DealResponseDto(Deal deal, String timeZone, BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto,
                           BasicMerchantBusinessResponseDto basicBankBusinessResponseDto) {
        super(deal, basicMerchantBusinessResponseDto, timeZone);
        this.bank = basicBankBusinessResponseDto;
        this.description = deal.getDescription();
        this.dealCode = deal.getDealCode();
        this.termsAndConditions = deal.getTermsAndConditions();
        this.primaryType = deal.getPrimaryType();
        this.secondaryType = deal.getSecondaryType();
        this.deductionAmount = deal.getDeductionAmount();
        this.deductionPercentage = deal.getDeductionPercentage();
        this.approvalStatus = deal.getApprovalStatus();
        this.deductionType = deal.getDeductionType();
        deal.getRelatedCategories().forEach(category -> categories.add(new CategoryResponseDto(category, timeZone)));
        deal.getRelatedBrands().forEach(brand -> brands.add(new BrandResponseDto(brand, timeZone)));
        this.createdAt = new DateResponseDto(deal.getCreatedAt(), timeZone, new Date(deal.getCreatedAt()));
        this.updatedAt = new DateResponseDto(deal.getUpdatedAt(), timeZone, new Date(deal.getUpdatedAt()));
    }


    @Override
    public String toLogJson() {
        return toJson();
    }
}
