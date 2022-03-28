package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.DeductionType;
import com.swivel.cc.base.enums.PrimaryType;
import com.swivel.cc.base.enums.SecondaryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Deal entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "bank_deal")
public class BankDeal {
    @Transient
    private static final String DEAL_ID_PREFIX = "did-";
    @Transient
    private static final int DESCRIPTION_MAX_LENGTH = 500;
    @Transient
    private static final int TERMS_AND_CONDITION_MAX_LENGTH = 500;


    @Id
    private String id;
    @Column(nullable = false)
    private String title;
    @Column(nullable = false)
    private String subTitle;
    @Size(max = DESCRIPTION_MAX_LENGTH)
    private String description;
    @Size(max = TERMS_AND_CONDITION_MAX_LENGTH)
    @Column(nullable = false)
    private String termsAndConditions;
    @ElementCollection
    private List<String> imageUrls;
    @Column(nullable = false)
    private String coverImage;
    @Column(nullable = false)
    private PrimaryType primaryType;
    @Column(nullable = false)
    private SecondaryType secondaryType;
    @Column(nullable = false)
    private int quantity;
    private double originalPrice;
    @Column(nullable = false)
    private DeductionType deductionType;
    private Double deductionAmount = null;
    private Double deductionPercentage = null;
    @Column(nullable = false)
    private long validFrom;
    @Column(nullable = false)
    private long expiredOn;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApprovalStatus approvalStatus;
    private String comment;
    @Column(nullable = false)
    private String bankId;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "bank_deal_category",
            joinColumns = @JoinColumn(name = "bankDealId"),
            inverseJoinColumns = @JoinColumn(name = "categoryId"))
    private Set<Category> relatedCategories = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "bank_deal_brand",
            joinColumns = @JoinColumn(name = "bankDealId"),
            inverseJoinColumns = @JoinColumn(name = "brandId"))
    private Set<Brand> relatedBrands = new HashSet<>();
    private String dealCode;
    private long createdAt;
    private long updatedAt;
    private boolean isDeleted;
    private String merchantId;

    public BankDeal(Deal deal) {
        this.id = deal.getId();
        this.title = deal.getTitle();
        this.subTitle = deal.getSubTitle();
        this.description = deal.getDescription();
        this.termsAndConditions = deal.getTermsAndConditions();
        this.imageUrls = deal.getImageUrls();
        this.coverImage = deal.getCoverImage();
        this.primaryType = deal.getPrimaryType();
        this.secondaryType = deal.getSecondaryType();
        this.quantity = deal.getQuantity();
        this.originalPrice = deal.getOriginalPrice();
        this.deductionType = deal.getDeductionType();
        this.deductionAmount = deal.getDeductionAmount();
        this.deductionPercentage = deal.getDeductionPercentage();
        this.validFrom = deal.getValidFrom();
        this.expiredOn = deal.getExpiredOn();
        this.approvalStatus = deal.getApprovalStatus();
        this.comment = deal.getComment();
        this.bankId = deal.getMerchantId();
        this.relatedBrands = deal.getRelatedBrands();
        this.relatedCategories = deal.getRelatedCategories();
        this.createdAt = deal.getCreatedAt();
        this.updatedAt = deal.getUpdatedAt();
        this.isDeleted = deal.isDeleted();
        this.merchantId = deal.getShopId();
        this.dealCode = deal.getDealCode();
    }
}
