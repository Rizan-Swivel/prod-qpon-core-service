package com.swivel.cc.base.domain.request;

import com.swivel.cc.base.enums.DeductionType;
import com.swivel.cc.base.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

/**
 * Deal request dto
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class DealRequestDto extends RequestDto {

    private static final int NETWORK_DELAY_IN_MILLISECONDS = 3600;
    private String title;
    private String subTitle;
    private String description;
    private String termsAndConditions;
    private List<String> imageUrls;
    private String coverImage;
    private double originalPrice;
    private int quantity;
    private DeductionType deductionType;
    private double deductionAmount;
    private double deductionPercentage;
    private long validFrom;
    private long expiredOn;
    private String merchantId;
    private List<String> brandIds;
    private List<String> categoryIds;
    private String shopId;

    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(title) && isNonEmpty(subTitle) && isNonEmpty(termsAndConditions)
                && isNonEmpty(merchantId) && categoryIds != null && !categoryIds.isEmpty() && deductionType != null
                && isNonEmpty(coverImage) && isDeductionValueAvailable() && !imageUrls.isEmpty();
    }

    /**
     * This method checks if required fields are available for user type.
     *
     * @param userType MERCHANT/BANK
     * @return true/false
     */
    public boolean isRequiredAvailable(UserType userType) {
        if (userType.equals(UserType.BANK)) {
            return isRequiredAvailable() && isNonEmpty(shopId);
        }
        return isRequiredAvailable();
    }

    /**
     * This method checks validFrom date is in future or not.
     * NETWORK_DELAY_IN_MILLISECONDS (1 minute) was introduce to discard the network delay.
     *
     * @return true / false
     */
    public boolean isValidStartDate() {
        return validFrom > System.currentTimeMillis() - NETWORK_DELAY_IN_MILLISECONDS;
    }

    /**
     * This method checks expireDate is in future or not
     *
     * @return true / false
     */
    public boolean isValidExpireDate() {
        return expiredOn > validFrom;
    }

    /**
     * This method checks originalPrice is more than 0
     *
     * @return true / false
     */
    public boolean isValidPrice() {
        return originalPrice >= 0;
    }

    /**
     * This method checks the brand id is available or not.
     *
     * @return true / false
     */
    public boolean isBrandIdsAvailable() {
        return brandIds != null && !brandIds.isEmpty();
    }

    /**
     * This method checks the amount or percentage values according to the deductionType
     */
    public boolean isDeductionValueAvailable() {
        if (deductionType.equals(DeductionType.AMOUNT)) {
            return deductionAmount != -1;
        } else {
            return deductionPercentage != -1;
        }
    }

    /**
     * This method checks the amount or percentage values are valid values.
     */
    public boolean isValidDeductionValue() {
        if (deductionType.equals(DeductionType.AMOUNT)) {
            return isValidDeductionAmount();
        } else {
            return isValidDeductionPercentage();
        }
    }

    /**
     * This method returns true when deduction amount is valid.
     *
     * @return true/ false
     */
    private boolean isValidDeductionAmount() {
        if (originalPrice > 0) {
            return deductionAmount >= 0 && (deductionAmount <= originalPrice);
        } else if (originalPrice == 0) {
            return deductionAmount >= 0;
        } else {
            return false;
        }
    }

    /**
     * This method returns true when deduction percentage is valid.
     *
     * @return true/ false
     */
    private boolean isValidDeductionPercentage() {
        if (originalPrice >= 0) {
            return deductionPercentage >= 0 && deductionPercentage <= 100;
        } else {
            return false;
        }
    }
}
