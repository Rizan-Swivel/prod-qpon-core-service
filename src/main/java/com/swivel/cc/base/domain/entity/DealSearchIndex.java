package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.enums.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.Size;

/**
 * This class should replace from a search engine - real-time update
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "search_deal")
public class DealSearchIndex {
    @Transient
    private static final int DESCRIPTION_MAX_LENGTH = 500;
    @Transient
    private static final int TERMS_AND_CONDITION_MAX_LENGTH = 500;
    @Transient
    private static final int SEARCH_IMAGE_URLS_MAX = 1000;
    @Transient
    private static final int RELATED_BRANDS_MAX_LENGTH = 1000;
    @Transient
    private static final int RELATED_CATEGORIES_MAX_LENGTH = 1000;
    @Transient
    private static final String SEPARATOR = ",";
    @Transient
    private static final String EMPTY_TEXT = "";

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
    @Size(max = SEARCH_IMAGE_URLS_MAX)
    private String searchImageUrls;
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
    @Size(max = RELATED_CATEGORIES_MAX_LENGTH)
    private String relatedCategories;
    @Size(max = RELATED_BRANDS_MAX_LENGTH)
    private String relatedBrands;
    private long createdAt;
    private long updatedAt;
    private boolean isDeleted;
    private String brandNames = EMPTY_TEXT;
    private String categoryNames = EMPTY_TEXT;
    private String merchantName = EMPTY_TEXT;
    private String merchantImageUrl = EMPTY_TEXT;
    private boolean isActiveMerchant;
    @Enumerated(EnumType.STRING)
    private UserType dealSource;
    private String dealCode;

    public DealSearchIndex(Deal deal, BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto,
                           UserType userType) {
        this.id = deal.getId();
        this.title = deal.getTitle();
        this.subTitle = deal.getSubTitle();
        this.description = deal.getDescription();
        this.termsAndConditions = deal.getTermsAndConditions();
        this.searchImageUrls = deal.getImageUrls().toString();
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
        this.approvalStatus = ApprovalStatus.PENDING;
        this.merchantId = deal.getMerchantId();
        deal.getRelatedBrands().forEach(brand -> {
            this.brandNames += brand.getName() + SEPARATOR;
            this.relatedBrands += brand.getId() + SEPARATOR;
        });
        deal.getRelatedCategories().forEach(category -> {
            this.categoryNames += category.getName() + SEPARATOR;
            this.relatedCategories += category.getId() + SEPARATOR;
        });
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.isDeleted = false;
        this.merchantName = basicMerchantBusinessResponseDto.getName();
        this.merchantImageUrl = basicMerchantBusinessResponseDto.getImageUrl();
        this.isActiveMerchant = basicMerchantBusinessResponseDto.isActive();
        this.dealSource = userType;
        this.dealCode = deal.getDealCode();
    }

    /**
     * Update dealSearchIndex.
     *
     * @param deal                        deal
     * @param businessMerchantResponseDto businessMerchantResponseDto
     */
    public void update(Deal deal, BusinessMerchantResponseDto businessMerchantResponseDto) {
        this.title = deal.getTitle();
        this.subTitle = deal.getSubTitle();
        this.description = deal.getDescription();
        this.termsAndConditions = deal.getTermsAndConditions();
        this.searchImageUrls = deal.getImageUrls().toString();
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
        this.brandNames = "";
        this.relatedBrands = "";
        deal.getRelatedBrands().forEach(brand -> {
            this.brandNames += brand.getName() + SEPARATOR;
            this.relatedBrands += brand.getId() + SEPARATOR;
        });
        this.categoryNames = "";
        this.relatedCategories = "";
        deal.getRelatedCategories().forEach(category -> {
            this.categoryNames += category.getName() + SEPARATOR;
            this.relatedCategories += category.getId() + SEPARATOR;
        });
        this.updatedAt = System.currentTimeMillis();
        this.merchantName = businessMerchantResponseDto.getBusinessName();
        this.merchantImageUrl = businessMerchantResponseDto.getImageUrl();
    }
}
