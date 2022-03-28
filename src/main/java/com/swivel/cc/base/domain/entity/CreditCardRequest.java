package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.request.CreditCardRequestCreateRequestDto;
import com.swivel.cc.base.domain.request.CreditCardRequestUpdateRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.util.UUID;

@Entity
@Table(name = "credit_card_request")
@Getter
@Setter
@NoArgsConstructor
public class CreditCardRequest {

    @Transient
    private static final String CARD_REQUEST_ID_PREFIX = "ccrid-";

    @Id
    private String id;
    private String userId;
    private String bankId;
    private String fullName;
    private String mobileNumber;
    private String email;
    private String nic;
    private String city;
    private String companyName;
    private String profession;
    private long createdAt;

    public CreditCardRequest(CreditCardRequestCreateRequestDto creditCardRequestCreateRequestDto) {
        this.id = CARD_REQUEST_ID_PREFIX + UUID.randomUUID();
        this.userId = creditCardRequestCreateRequestDto.getUserId();
        this.bankId = creditCardRequestCreateRequestDto.getBankId();
        this.fullName = creditCardRequestCreateRequestDto.getFullName().trim();
        this.mobileNumber = creditCardRequestCreateRequestDto.getMobileNumber().getNo();
        this.email = creditCardRequestCreateRequestDto.getEmail().trim();
        this.nic = creditCardRequestCreateRequestDto.getNic().trim();
        this.city = creditCardRequestCreateRequestDto.getCity().trim();
        this.companyName = (creditCardRequestCreateRequestDto.getCompanyName() != null) ?
                creditCardRequestCreateRequestDto.getCompanyName().trim() : null;
        this.profession = (creditCardRequestCreateRequestDto.getProfession() != null) ?
                creditCardRequestCreateRequestDto.getProfession().trim() : null;
        this.createdAt = System.currentTimeMillis();
    }

    public CreditCardRequest(CreditCardRequestUpdateRequestDto cardRequestUpdateRequestDto) {
        this.id = cardRequestUpdateRequestDto.getId();
        this.userId = cardRequestUpdateRequestDto.getUserId();
        this.bankId = cardRequestUpdateRequestDto.getBankId();
        this.fullName = cardRequestUpdateRequestDto.getFullName().trim();
        this.mobileNumber = cardRequestUpdateRequestDto.getMobileNumber().getNo();
        this.email = cardRequestUpdateRequestDto.getEmail().trim();
        this.nic = cardRequestUpdateRequestDto.getNic().trim();
        this.city = cardRequestUpdateRequestDto.getCity().trim();
        this.companyName = (cardRequestUpdateRequestDto.getCompanyName() != null) ?
                cardRequestUpdateRequestDto.getCompanyName().trim() : null;
        this.profession = (cardRequestUpdateRequestDto.getProfession() != null) ?
                cardRequestUpdateRequestDto.getProfession().trim() : null;
    }
}
