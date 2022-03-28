package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.CreditCardRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;

@Setter
@Getter
@AllArgsConstructor
public class DetailCreditCardRequestResponseDto extends ResponseDto {

    private String id;
    private String userId;
    private String bankId;
    private String fullName;
    private MobileNoResponseDto mobileNumber;
    private String email;
    private String nic;
    private String city;
    private String companyName;
    private String profession;
    private DateResponseDto requestedOn;

    public DetailCreditCardRequestResponseDto(CreditCardRequest creditCardRequest, String timeZone) {
        this.id = creditCardRequest.getId();
        this.userId = creditCardRequest.getUserId();
        this.bankId = creditCardRequest.getBankId();
        this.fullName = creditCardRequest.getFullName();
        this.mobileNumber = new MobileNoResponseDto(creditCardRequest.getMobileNumber());
        this.email = creditCardRequest.getEmail();
        this.nic = creditCardRequest.getNic();
        this.city = creditCardRequest.getCity();
        this.companyName = creditCardRequest.getCompanyName();
        this.profession = creditCardRequest.getProfession();
        this.requestedOn = new DateResponseDto(
                creditCardRequest.getCreatedAt(), timeZone, new Date(creditCardRequest.getCreatedAt()));
    }
}
