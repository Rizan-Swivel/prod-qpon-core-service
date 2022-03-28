package com.swivel.cc.base.domain.request;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CreditCardRequestCreateRequestDto extends RequestDto {

    private String bankId;
    private String userId;
    private String fullName;
    private MobileNoRequestDto mobileNumber;
    private String email;
    private String nic;
    private String city;
    private String companyName;
    private String profession;

    @Override
    public String toLogJson() {
        return toJson();
    }

    /**
     * This method checks all required fields are available.
     *
     * @return true/ false
     */
    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(bankId) && isNonEmpty(userId)
                && isNonEmpty(fullName) && isNonEmpty(nic)
                && isNonEmpty(city) && isNonEmpty(email)
                && isNonEmpty(mobileNumber.getNo());
    }
}
