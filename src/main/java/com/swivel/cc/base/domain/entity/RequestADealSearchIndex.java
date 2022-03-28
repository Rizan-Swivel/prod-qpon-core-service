package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.enums.UserType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

/**
 * This class should replace from a search engine - real-time update
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "search_request_a_deal")
public class RequestADealSearchIndex {

    @Id
    private String id;
    @Column(nullable = false)
    private String userId;
    @Column(nullable = false)
    private String merchantId;
    @Column(nullable = false)
    private String merchantName;
    @Column(nullable = false)
    private String categoryId;
    @Column(nullable = false)
    private String categoryName;
    private String brandId;
    private String brandName;
    @Column(nullable = false)
    private String note;
    @Enumerated(EnumType.STRING)
    private UserType toUserType;
    private long createdAt;
    private long updatedAt;

    public RequestADealSearchIndex(RequestADeal requestADeal, String merchantName) {
        this.id = requestADeal.getId();
        this.userId = requestADeal.getUserId();
        this.merchantId = requestADeal.getMerchantId();
        this.merchantName = merchantName;
        this.categoryId = requestADeal.getCategory().getId();
        this.categoryName = requestADeal.getCategory().getName();
        this.note = requestADeal.getNote();
        this.createdAt = requestADeal.getCreatedAt();
        this.updatedAt = requestADeal.getUpdatedAt();
        if (requestADeal.getBrand() == null) {
            this.brandId = null;
            this.brandName = null;
        } else {
            this.brandId = requestADeal.getBrand().getId();
            this.brandName = requestADeal.getBrand().getName();
        }
        this.toUserType = requestADeal.getToUserType();
    }
}
