package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.Setter;

/**
 * Price response dto tpo show deal price
 */
@Setter
@Getter
public class PriceResponseDto extends ResponseDto {

    private static final String RS = " Rs. ";
    private static final String SAVE = "Save Rs. ";

    private double originalPrice;
    private String originalDisplayPrice;
    private double currentPrice;
    private String currentDisplayPrice;
    private double savedAmount;
    private String savedDisplayAmount;

    public PriceResponseDto(double price, Double deductionValue, Double deductionPercentage) {
        this.originalPrice = price;
        this.originalDisplayPrice = RS + (int) price;

        if (deductionValue != null) {
            this.currentPrice = price - deductionValue;
            this.savedAmount = deductionValue;
        } else {
            this.currentPrice = price - (price * deductionPercentage / 100);
            this.savedAmount = price * deductionPercentage / 100;
        }

        this.currentDisplayPrice = RS + (int) currentPrice;
        this.savedDisplayAmount = SAVE + (int) savedAmount;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }
}
