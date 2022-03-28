package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * This class should replace from a search engine - real-time update
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_merchant_search")
public class BankMerchantSearchIndex {

    @Transient
    private static final String BANK_MERCHANT_ID_PREFIX = "bms-";

    @Id
    private String id;
    @Column(nullable = false)
    private String fromLatestDealId;
    @Column(nullable = false)
    private String merchantId;
    @Column(nullable = false)
    private String merchantName;
    @Column(nullable = false)
    private boolean isActiveMerchant;
    @Column(nullable = false)
    private String bankId;
    @Column(nullable = false)
    private String bankName;
    @Column(nullable = false)
    private boolean isActiveBank;

    public BankMerchantSearchIndex(String bankDealId,
                                   BasicMerchantBusinessResponseDto bank,
                                   BusinessMerchantResponseDto merchant) {
        this.id = BANK_MERCHANT_ID_PREFIX + UUID.randomUUID();
        this.fromLatestDealId = bankDealId;
        this.merchantId = merchant.getMerchantId();
        this.merchantName = merchant.getBusinessName();
        this.isActiveMerchant = merchant.isActive();
        this.bankId = bank.getId();
        this.bankName = bank.getName();
        this.isActiveBank = bank.isActive();

    }
}
