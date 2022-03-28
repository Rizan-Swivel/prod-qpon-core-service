package com.swivel.cc.base.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Date;
import java.util.UUID;

/**
 * Deals of the day entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_deals_of_the_day")
public class BankDealsOfTheDay {
    private static final String BANK_DEALS_OF_THE_DAY_ID_PREFIX = "bdodid-";
    @Id
    private String id;
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "bankDealId")
    private BankDeal bankDeal;
    @Column(nullable = false)
    private Date applyOn;
    private boolean isActive;

    public BankDealsOfTheDay(BankDeal bankDeal) {
        this.id = BANK_DEALS_OF_THE_DAY_ID_PREFIX + UUID.randomUUID();
        this.bankDeal = bankDeal;
        this.applyOn = new Date(System.currentTimeMillis());
        this.isActive = true;
    }
}