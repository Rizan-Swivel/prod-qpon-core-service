package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.DealOfTheDaySearchIndex;
import com.swivel.cc.base.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

/**
 * This class should replace from a search engine.
 */
public interface DealsOfTheDaySearchIndexRepository extends JpaRepository<DealOfTheDaySearchIndex, String> {

    String DESCRIPTION = " d.description LIKE %:searchTerm% ";
    String CATEGORY_NAMES = " d.categoryNames LIKE %:searchTerm% ";
    String MERCHANT_NAME = " d.merchantName LIKE %:searchTerm% ";
    String MERCHANT_ID = " d.merchantId=:merchantId ";
    String DEAL_SOURCE = " d.dealSource=:dealSource ";
    String RELATED_CATEGORIES = " d.relatedCategories LIKE %:categoryId% ";
    String MERCHANT_NAME_CATEGORY_NAMES = MERCHANT_NAME + " OR " + CATEGORY_NAMES;
    String TITLE_SUBTITLE_BRAND_NAME =
            " d.title LIKE %:searchTerm% OR d.subTitle LIKE %:searchTerm% OR d.brandNames LIKE %:searchTerm% ";
    String TITLE_SUBTITLE_BRAND_NAME_DESCRIPTION = TITLE_SUBTITLE_BRAND_NAME + " OR " + DESCRIPTION;

    /**
     * This method finds all approved deals by search.
     *
     * @param pageable   pageable
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d from DealOfTheDaySearchIndex d WHERE " + DEAL_SOURCE + " AND ( " +
            TITLE_SUBTITLE_BRAND_NAME_DESCRIPTION + " OR " + MERCHANT_NAME_CATEGORY_NAMES + " ) ")
    Page<DealOfTheDaySearchIndex> findAllApprovedDealsBySearchTermAndDealSource(Pageable pageable,
                                                                                @Param("searchTerm") String searchTerm,
                                                                                @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals
     *
     * @param pageable pageable
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d FROM DealOfTheDaySearchIndex d WHERE " + DEAL_SOURCE)
    Page<DealOfTheDaySearchIndex> findAllSearchDealsByDealSource(Pageable pageable, @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals for merchantId
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d FROM DealOfTheDaySearchIndex d WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE)
    Page<DealOfTheDaySearchIndex> findAllByMerchantIdAndDealSource(Pageable pageable, @Param("merchantId") String merchantId,
                                                                   @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals for categoryId
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d FROM DealOfTheDaySearchIndex d WHERE " + DEAL_SOURCE + " AND " + RELATED_CATEGORIES)
    Page<DealOfTheDaySearchIndex> findAllByCategoryIdAndDealSource(Pageable pageable, @Param("categoryId") String categoryId,
                                                                   @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals for categoryId and searchTerm
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d from DealOfTheDaySearchIndex d WHERE " + DEAL_SOURCE + " AND " + RELATED_CATEGORIES + " AND ( "
            + TITLE_SUBTITLE_BRAND_NAME + " OR " + CATEGORY_NAMES + " )")
    Page<DealOfTheDaySearchIndex> findAllByCategoryIdAndSearchTermAndDealSource(Pageable pageable,
                                                                                @Param("categoryId") String categoryId,
                                                                                @Param("searchTerm") String searchTerm,
                                                                                @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals for merchantId and categoryId
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param categoryId categoryId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d FROM DealOfTheDaySearchIndex d WHERE " + DEAL_SOURCE + " AND " + RELATED_CATEGORIES +
            " AND " + MERCHANT_ID)
    Page<DealOfTheDaySearchIndex> findAllByMerchantIdAndCategoryIdAndDealSource(Pageable pageable,
                                                                                @Param("merchantId") String merchantId,
                                                                                @Param("categoryId") String categoryId,
                                                                                @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals for merchantId and searchTerm
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d from DealOfTheDaySearchIndex d WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE + " AND ( " +
            TITLE_SUBTITLE_BRAND_NAME_DESCRIPTION + " OR " + MERCHANT_NAME_CATEGORY_NAMES + " )")
    Page<DealOfTheDaySearchIndex> findAllByMerchantIdAndSearchTermAndDealSource(Pageable pageable,
                                                                                @Param("merchantId") String merchantId,
                                                                                @Param("searchTerm") String searchTerm,
                                                                                @Param("dealSource") UserType dealSource);

    /**
     * This method find all deals for categoryId, merchantId and searchTerm
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param categoryId categoryId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d from DealOfTheDaySearchIndex d WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE + " AND "
            + RELATED_CATEGORIES + " AND ( " + TITLE_SUBTITLE_BRAND_NAME_DESCRIPTION + " OR " +
            MERCHANT_NAME_CATEGORY_NAMES + " )")
    Page<DealOfTheDaySearchIndex> findAllByMerchantIdCategoryIdSearchTermAndDealSource(Pageable pageable,
                                                                                       @Param("merchantId") String merchantId,
                                                                                       @Param("categoryId") String categoryId,
                                                                                       @Param("searchTerm") String searchTerm,
                                                                                       @Param("dealSource") UserType dealSource);

    /**
     * This method will return DealOfTheDaySearchIndex List by merchant ids
     *
     * @param merchantIds merchantIds List
     * @return DealOfTheDaySearchIndex List
     */
    List<DealOfTheDaySearchIndex> getAllByMerchantIdIn(List<String> merchantIds);
}

