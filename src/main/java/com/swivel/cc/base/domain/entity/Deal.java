package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.request.DealRequestDto;
import com.swivel.cc.base.domain.request.DealUpdateRequestDto;
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
import java.util.UUID;

/**
 * Deal entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "deal")
public class Deal {
    @Transient
    private static final String DEAL_ID_PREFIX = "did-";
    @Transient
    private static final int DESCRIPTION_MAX_LENGTH = 500;
    @Transient
    private static final int TERMS_AND_CONDITION_MAX_LENGTH = 500;
    @Transient
    private String shopId;


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
    private String merchantId;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "deal_category",
            joinColumns = @JoinColumn(name = "dealId"),
            inverseJoinColumns = @JoinColumn(name = "categoryId"))
    private Set<Category> relatedCategories = new HashSet<>();
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "deal_brand",
            joinColumns = @JoinColumn(name = "dealId"),
            inverseJoinColumns = @JoinColumn(name = "brandId"))
    private Set<Brand> relatedBrands = new HashSet<>();
    private String dealCode;
    private long createdAt;
    private long updatedAt;
    private boolean isDeleted;

    public Deal(DealRequestDto dealRequestDto, Set<Brand> brands, Set<Category> categories, String dealCode) {
        this.id = DEAL_ID_PREFIX + UUID.randomUUID();
        this.title = dealRequestDto.getTitle();
        this.subTitle = dealRequestDto.getSubTitle();
        this.description = dealRequestDto.getDescription();
        this.termsAndConditions = dealRequestDto.getTermsAndConditions();
        this.imageUrls = dealRequestDto.getImageUrls();
        this.coverImage = dealRequestDto.getCoverImage();
        this.primaryType = PrimaryType.READY_MADE;
        this.secondaryType = SecondaryType.DEAL;
        this.quantity = dealRequestDto.getQuantity();
        this.originalPrice = dealRequestDto.getOriginalPrice();
        this.deductionType = dealRequestDto.getDeductionType();
        this.deductionAmount = dealRequestDto.getDeductionAmount();
        this.deductionPercentage = dealRequestDto.getDeductionPercentage();
        this.validFrom = dealRequestDto.getValidFrom();
        this.expiredOn = dealRequestDto.getExpiredOn();
        this.approvalStatus = ApprovalStatus.PENDING;
        this.merchantId = dealRequestDto.getMerchantId();
        this.relatedBrands = brands;
        this.relatedCategories = categories;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isDeleted = false;
        this.dealCode = dealCode;
    }

    public Deal(BankDeal bankDeal) {
        this.id = bankDeal.getId();
        this.title = bankDeal.getTitle();
        this.subTitle = bankDeal.getSubTitle();
        this.description = bankDeal.getDescription();
        this.termsAndConditions = bankDeal.getTermsAndConditions();
        this.imageUrls = bankDeal.getImageUrls();
        this.coverImage = bankDeal.getCoverImage();
        this.primaryType = bankDeal.getPrimaryType();
        this.secondaryType = bankDeal.getSecondaryType();
        this.quantity = bankDeal.getQuantity();
        this.originalPrice = bankDeal.getOriginalPrice();
        this.deductionType = bankDeal.getDeductionType();
        this.deductionAmount = bankDeal.getDeductionAmount();
        this.deductionPercentage = bankDeal.getDeductionPercentage();
        this.validFrom = bankDeal.getValidFrom();
        this.expiredOn = bankDeal.getExpiredOn();
        this.approvalStatus = bankDeal.getApprovalStatus();
        this.comment = bankDeal.getComment();
        this.merchantId = bankDeal.getBankId();
        this.relatedBrands = bankDeal.getRelatedBrands();
        this.relatedCategories = bankDeal.getRelatedCategories();
        this.createdAt = bankDeal.getCreatedAt();
        this.updatedAt = bankDeal.getUpdatedAt();
        this.isDeleted = bankDeal.isDeleted();
        this.shopId = bankDeal.getMerchantId();
        this.dealCode = bankDeal.getDealCode();
    }

    /**
     * Update deal.
     *
     * @param dealUpdateRequestDto dealUpdateRequestDto
     * @param categories           categories list
     * @param brands               brands list
     */
    public void update(DealUpdateRequestDto dealUpdateRequestDto, Set<Category> categories, Set<Brand> brands) {
        this.title = dealUpdateRequestDto.getTitle();
        this.subTitle = dealUpdateRequestDto.getSubTitle();
        this.description = dealUpdateRequestDto.getDescription();
        this.termsAndConditions = dealUpdateRequestDto.getTermsAndConditions();
        this.imageUrls = dealUpdateRequestDto.getImageUrls();
        this.coverImage = dealUpdateRequestDto.getCoverImage();
        this.quantity = dealUpdateRequestDto.getQuantity();
        this.deductionType = dealUpdateRequestDto.getDeductionType();
        if (dealUpdateRequestDto.getDeductionType() == DeductionType.PERCENTAGE) {
            this.deductionAmount = null;
            this.deductionPercentage = dealUpdateRequestDto.getDeductionPercentage();
        }
        if (dealUpdateRequestDto.getDeductionType() == DeductionType.AMOUNT) {
            this.deductionAmount = dealUpdateRequestDto.getDeductionAmount();
            this.deductionPercentage = null;
        }
        this.originalPrice = dealUpdateRequestDto.getOriginalPrice();
        this.validFrom = dealUpdateRequestDto.getValidFrom();
        this.expiredOn = dealUpdateRequestDto.getExpiredOn();
        this.relatedBrands = brands;
        this.relatedCategories = categories;
        this.merchantId = dealUpdateRequestDto.getMerchantId();
        this.updatedAt = System.currentTimeMillis();
        this.shopId = dealUpdateRequestDto.getShopId();
    }
}
