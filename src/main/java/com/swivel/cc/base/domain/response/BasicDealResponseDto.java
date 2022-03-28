package com.swivel.cc.base.domain.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.swivel.cc.base.domain.entity.AuthUser;
import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.entity.DealSearchIndex;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.UserType;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Basic deal request dto
 */
@Setter
@Getter
public class BasicDealResponseDto extends ResponseDto {
    private String id;
    private String title;
    private String subTitle;
    private String dealCode;
    private List<String> imageUrls = new ArrayList<>();
    private String coverImage;
    private int totalQuantity;
    private int remainingQuantity;
    private PriceResponseDto price;
    private Double deductionPercentage;
    private DateResponseDto validFrom;
    private DateResponseDto expiredOn;
    private ApprovalStatus approvalStatus;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BasicMerchantBusinessResponseDto merchant;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private BasicMerchantBusinessResponseDto bank;
    private String dealSource;

    public BasicDealResponseDto(Deal deal, BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto, String timeZone) {
        this.id = deal.getId();
        this.title = deal.getTitle();
        this.subTitle = deal.getSubTitle();
        this.imageUrls = deal.getImageUrls();
        this.coverImage = deal.getCoverImage();
        this.totalQuantity = deal.getQuantity();
        this.remainingQuantity = deal.getQuantity();
        this.price = new PriceResponseDto(deal.getOriginalPrice(), deal.getDeductionAmount(),
                deal.getDeductionPercentage());
        this.deductionPercentage = deal.getDeductionPercentage();
        this.validFrom = new DateResponseDto(deal.getValidFrom(), timeZone, new Date(deal.getValidFrom()));
        this.expiredOn = new DateResponseDto(deal.getExpiredOn(), timeZone, new Date(deal.getExpiredOn()));
        this.dealSource = basicMerchantBusinessResponseDto.getUserType();
        if (dealSource.equals(UserType.MERCHANT.toString()))
            this.merchant = basicMerchantBusinessResponseDto;
        else
            this.bank = basicMerchantBusinessResponseDto;
        this.approvalStatus = deal.getApprovalStatus();
        this.dealCode = deal.getDealCode();
    }

    public BasicDealResponseDto(DealSearchIndex dealSearchIndex, String timeZone) {
        this.id = dealSearchIndex.getId();
        this.title = dealSearchIndex.getTitle();
        this.subTitle = dealSearchIndex.getSubTitle();
        this.imageUrls.addAll(stringToListImages(dealSearchIndex.getSearchImageUrls()));
        this.coverImage = dealSearchIndex.getCoverImage();
        this.totalQuantity = dealSearchIndex.getQuantity();
        this.remainingQuantity = dealSearchIndex.getQuantity();
        this.deductionPercentage = dealSearchIndex.getDeductionPercentage();
        this.price = new PriceResponseDto(dealSearchIndex.getOriginalPrice(), dealSearchIndex.getDeductionAmount(),
                dealSearchIndex.getDeductionPercentage());
        this.validFrom = new DateResponseDto(dealSearchIndex.getValidFrom(), timeZone,
                new Date(dealSearchIndex.getValidFrom()));
        this.expiredOn = new DateResponseDto(dealSearchIndex.getExpiredOn(), timeZone,
                new Date(dealSearchIndex.getExpiredOn()));
        this.dealSource = dealSearchIndex.getDealSource().toString();
        if (dealSource.equals(UserType.MERCHANT.toString()))
            this.merchant = new BasicMerchantBusinessResponseDto(dealSearchIndex.getMerchantId(),
                    new AuthUser(dealSearchIndex.getMerchantName(), dealSearchIndex.getMerchantImageUrl()));
        else
            this.bank = new BasicMerchantBusinessResponseDto(dealSearchIndex.getMerchantId(),
                    new AuthUser(dealSearchIndex.getMerchantName(), dealSearchIndex.getMerchantImageUrl()));
        this.approvalStatus = dealSearchIndex.getApprovalStatus();
        this.dealCode = dealSearchIndex.getDealCode();
    }

    public BasicDealResponseDto(BankDeal bankDeal, BasicMerchantBusinessResponseDto merchantBusinessResponseDto,
                                BasicMerchantBusinessResponseDto bankBusinessResponseDto, String timeZone) {
        this.id = bankDeal.getId();
        this.title = bankDeal.getTitle();
        this.subTitle = bankDeal.getSubTitle();
        this.imageUrls = bankDeal.getImageUrls();
        this.coverImage = bankDeal.getCoverImage();
        this.totalQuantity = bankDeal.getQuantity();
        this.remainingQuantity = bankDeal.getQuantity();
        this.price = new PriceResponseDto(bankDeal.getOriginalPrice(), bankDeal.getDeductionAmount(),
                bankDeal.getDeductionPercentage());
        this.deductionPercentage = bankDeal.getDeductionPercentage();
        this.validFrom = new DateResponseDto(bankDeal.getValidFrom(), timeZone, new Date(bankDeal.getValidFrom()));
        this.expiredOn = new DateResponseDto(bankDeal.getExpiredOn(), timeZone, new Date(bankDeal.getExpiredOn()));
        this.merchant = merchantBusinessResponseDto;
        this.bank = bankBusinessResponseDto;
        this.dealSource = UserType.BANK.toString();
        this.approvalStatus = bankDeal.getApprovalStatus();
        this.dealCode = bankDeal.getDealCode();
    }

    /**
     * This method convert string to string list
     *
     * @param images images string
     * @return images list
     */
    private List<String> stringToListImages(String images) {
        List<String> imageList =
                new ArrayList<>(Arrays.asList(images.trim().
                        replaceAll("[\\[\\]]", "").
                        split("\\s*,\\s*")));
        imageList.removeAll(Arrays.asList(null, ""));
        return imageList;
    }


    @Override
    public String toLogJson() {
        return toJson();
    }
}
