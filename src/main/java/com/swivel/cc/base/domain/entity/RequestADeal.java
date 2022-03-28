package com.swivel.cc.base.domain.entity;


import com.swivel.cc.base.domain.request.RequestADealCreateRequestDto;
import com.swivel.cc.base.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * RequestDeal entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "request_a_deal")

public class RequestADeal {

    @Transient
    private static final String REQUEST_A_DEAL_ID_PREFIX = "rdid-";

    @Id
    private String id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String merchantId;
    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private Category category;
    @ManyToOne
    @JoinColumn(name = "brand_id")
    private Brand brand;
    @Column(nullable = false)
    private String note;
    private String products;
    @Enumerated(EnumType.STRING)
    private UserType toUserType;
    @ManyToOne
    @JoinColumn(name = "offerType_id")
    private OfferType offerType;
    private long createdAt;
    private long updatedAt;

    public RequestADeal(RequestADealCreateRequestDto requestADealCreateRequestDto,
                        Category category, Brand brand, OfferType offerType) {
        this.id = REQUEST_A_DEAL_ID_PREFIX + UUID.randomUUID();
        this.userId = requestADealCreateRequestDto.getUserId();
        this.merchantId = requestADealCreateRequestDto.getMerchantId();
        this.category = category;
        this.brand = brand;
        this.note = requestADealCreateRequestDto.getNote();
        this.products = requestADealCreateRequestDto.getProducts();
        this.offerType = offerType;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }
}
