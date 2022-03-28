package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.DealSearchIndex;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * This class should replace from a search engine.
 */
public interface DealSearchIndexRepository extends JpaRepository<DealSearchIndex, String> {

    String ACTIVE_DEAL = "ds.validFrom <=:currentMills AND ds.expiredOn > :currentMills";
    String VALID_DEAL = "ds.isDeleted=false AND ds.approvalStatus='APPROVED'";
    String ACTIVE_MERCHANT = "ds.isActiveMerchant=true";
    String VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT = VALID_DEAL + " AND " + ACTIVE_DEAL + " AND " + ACTIVE_MERCHANT;
    String SEARCH =
            "(ds.title LIKE %:searchTerm% OR ds.subTitle LIKE %:searchTerm% OR ds.description" +
                    " LIKE %:searchTerm% OR ds.merchantName LIKE %:searchTerm% OR ds.brandNames LIKE %:searchTerm%" +
                    " OR ds.categoryNames LIKE %:searchTerm% OR ds.dealCode LIKE %:searchTerm%)";
    String MERCHANT_ID = "ds.merchantId=:merchantId";
    String RELATED_CATEGORIES = "ds.relatedCategories LIKE %:categoryId%";
    String VALID_ACTIVE_DEALS = VALID_DEAL + " AND " + ACTIVE_DEAL;
    String DEAL_SOURCE = "ds.dealSource=:dealSource";

    /**
     * This method find the filter deal by given Id which not deleted and approval status pending.
     *
     * @param id deal Id
     * @return optional dealSearchIndex
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.id=?1 AND d.is_deleted = false AND" +
            " d.approval_status = 'PENDING'", nativeQuery = true)
    Optional<DealSearchIndex> findPendingDealById(String id);

    /**
     * This method finds all not deleted deals by searchTerm.
     *
     * @param pageable   pageable
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d from DealSearchIndex d WHERE d.isDeleted = false AND d.dealSource=:userType AND (" +
            " d.title LIKE %:searchTerm% OR d.subTitle LIKE %:searchTerm% OR d.description LIKE %:searchTerm% OR" +
            " d.merchantName LIKE %:searchTerm% OR d.brandNames LIKE %:searchTerm% OR d.categoryNames" +
            " LIKE %:searchTerm% OR d.dealCode LIKE %:searchTerm%)")
    Page<DealSearchIndex> findAllNotDeletedDealsBySearchTerm(Pageable pageable, @Param("searchTerm") String searchTerm,
                                                             @Param("userType") UserType userType);

    /**
     * This method find all not deleted deals.
     *
     * @param pageable pageable
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d FROM DealSearchIndex d WHERE d.isDeleted = false AND d.dealSource=?1")
    Page<DealSearchIndex> findAllNotDeletedDeals(Pageable pageable, UserType userType);

    /**
     * This method find all not deleted deals for merchantId.
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d FROM DealSearchIndex d WHERE d.merchantId=?1 AND d.isDeleted = false ")
    Page<DealSearchIndex> findAllNotDeletedDealsByMerchantId(Pageable pageable, String merchantId);

    /**
     * This method find all not deleted deals for merchantId and searchTerm.
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT d from DealSearchIndex d WHERE d.merchantId=:merchantId AND d.isDeleted = false " +
            " AND ( d.title LIKE %:searchTerm% OR d.subTitle LIKE %:searchTerm% OR d.description LIKE %:searchTerm%" +
            " OR d.brandNames LIKE %:searchTerm% OR d.categoryNames LIKE %:searchTerm% " +
            "OR d.dealCode LIKE %:searchTerm%)")
    Page<DealSearchIndex> findAllNotDeletedDealsByMerchantIdAndSearchTerm(Pageable pageable,
                                                                          @Param("merchantId") String merchantId,
                                                                          @Param("searchTerm") String searchTerm);

    /**
     * This method finds all approved deals by search.
     *
     * @param pageable   pageable
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * from search_deal d WHERE d.deal_source=:dealSource AND d.is_deleted = false" +
            " AND d.approval_status = 'APPROVED' AND (d.title LIKE %:searchTerm% OR d.sub_title" +
            " LIKE %:searchTerm% OR d.description LIKE %:searchTerm% OR d.merchant_name LIKE" +
            " %:searchTerm% OR d.brand_names LIKE %:searchTerm% OR d.category_names LIKE %:searchTerm% " +
            " OR d.deal_code LIKE %:searchTerm% )",
            nativeQuery = true)
    Page<DealSearchIndex> findAllApprovedDealsBySearchTerm(Pageable pageable,
                                                           @Param("searchTerm") String searchTerm,
                                                           @Param("dealSource") String dealSource);

    /**
     * This method find all deals
     *
     * @param pageable pageable
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.deal_source=:dealSource AND d.is_deleted = false AND" +
            " d.approval_status = 'APPROVED'", nativeQuery = true)
    Page<DealSearchIndex> findAllSearchDeals(Pageable pageable,
                                             @Param("dealSource") String dealSource);

    /**
     * This method find all deals for merchantId
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.deal_source=:dealSource AND d.merchant_id=:merchantId AND" +
            " d.is_deleted = false AND d.approval_status = 'APPROVED'",
            nativeQuery = true)
    Page<DealSearchIndex> findAllByMerchantId(Pageable pageable, String merchantId,
                                              @Param("dealSource") String dealSource);

    /**
     * This method find all deals for brand id
     *
     * @param pageable pageable
     * @param brandId  brandId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.deal_source=:dealSource AND d.is_deleted = false" +
            " AND d.related_brands LIKE %:brandId%", nativeQuery = true)
    Page<DealSearchIndex> findAllByBrandId(Pageable pageable, String brandId,
                                           @Param("dealSource") String dealSource);

    /**
     * This method find all deals for brand id and searchTerm
     *
     * @param pageable   pageable
     * @param brandId    brandId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * from search_deal d WHERE d.deal_source=:dealSource AND d.is_deleted = false AND" +
            " d.related_brands LIKE %:brandId% AND ( d.title LIKE %:searchTerm% OR" +
            " d.sub_title LIKE %:searchTerm% OR d.brand_names LIKE %:searchTerm% OR d.deal_code LIKE %:searchTerm% )",
            nativeQuery = true)
    Page<DealSearchIndex> findAllByBrandIdAndSearchTerm(Pageable pageable,
                                                        @Param("brandId") String brandId,
                                                        @Param("searchTerm") String searchTerm,
                                                        @Param("dealSource") String dealSource);

    /**
     * This method find all deals for categoryId
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.deal_source=:dealSource AND d.is_deleted = false" +
            " AND d.related_categories LIKE %:categoryId%", nativeQuery = true)
    Page<DealSearchIndex> findAllByCategoryId(Pageable pageable, @Param("categoryId") String categoryId,
                                              @Param("dealSource") String dealSource);

    /**
     * This method find all deals for categoryId and searchTerm
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * from search_deal d WHERE d.deal_source=:dealSource AND d.is_deleted = false AND" +
            " d.related_categories LIKE %:categoryId% AND ( d.title LIKE %:searchTerm% OR" +
            " d.sub_title LIKE %:searchTerm% OR d.brand_names LIKE %:searchTerm% OR d.category_names" +
            " LIKE %:searchTerm% OR d.deal_code LIKE %:searchTerm% )", nativeQuery = true)
    Page<DealSearchIndex> findAllByCategoryIdAndSearchTerm(Pageable pageable,
                                                           @Param("categoryId") String categoryId,
                                                           @Param("searchTerm") String searchTerm,
                                                           @Param("dealSource") String dealSource);

    /**
     * This method find all deals for merchantId and categoryId
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param categoryId categoryId
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.merchant_id=:merchantId AND d.deal_source=:dealSource" +
            " AND d.is_deleted = false AND d.approval_status = 'APPROVED' AND d.related_categories LIKE %:categoryId%",
            nativeQuery = true)
    Page<DealSearchIndex> findAllByMerchantIdAndCategoryId(Pageable pageable,
                                                           @Param("merchantId") String merchantId,
                                                           @Param("categoryId") String categoryId,
                                                           @Param("dealSource") String dealSource);

    /**
     * This method find all deals for merchantId and searchTerm
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * from search_deal d WHERE d.merchant_id=:merchantId AND d.deal_source=:dealSource" +
            " AND d.is_deleted = false AND d.approval_status = 'APPROVED' AND ( d.title LIKE %:searchTerm%" +
            " OR d.sub_title LIKE %:searchTerm% OR d.description LIKE %:searchTerm%" +
            " OR d.merchant_name LIKE %:searchTerm% OR d.brand_names LIKE %:searchTerm% " +
            " OR d.category_names LIKE %:searchTerm% OR d.deal_code LIKE %:searchTerm% )", nativeQuery = true)
    Page<DealSearchIndex> findAllByMerchantIdAndSearchTerm(Pageable pageable,
                                                           @Param("merchantId") String merchantId,
                                                           @Param("searchTerm") String searchTerm,
                                                           @Param("dealSource") String dealSource);

    /**
     * This method find all deals for categoryId, merchantId and searchTerm
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @param categoryId categoryId
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * from search_deal d WHERE d.merchant_id=:merchantId AND d.deal_source=:dealSource AND d.is_deleted = false AND" +
            " d.approval_status = 'APPROVED' AND d.related_categories LIKE %:categoryId% AND" +
            " ( d.title LIKE %:searchTerm% OR d.sub_title LIKE %:searchTerm% OR d.description" +
            " LIKE %:searchTerm% OR d.merchant_name LIKE %:searchTerm% OR d.brand_names LIKE %:searchTerm%" +
            " OR d.category_names LIKE %:searchTerm% OR d.deal_code LIKE %:searchTerm% )", nativeQuery = true)
    Page<DealSearchIndex> findAllByMerchantIdCategoryIdSearchTerm(Pageable pageable,
                                                                  @Param("merchantId") String merchantId,
                                                                  @Param("categoryId") String categoryId,
                                                                  @Param("searchTerm") String searchTerm,
                                                                  @Param("dealSource") String dealSource);

    /**
     * This method finds all pending deals by search
     *
     * @param pageable   pageable
     * @param searchTerm searchTerm
     * @return dealSearchIndex page
     */
    @Query(value = "SELECT * from search_deal d WHERE d.is_deleted = false AND d.approval_status = 'PENDING' AND " +
            " d.deal_source=:userType AND ( d.title LIKE %:searchTerm% OR d.sub_title LIKE %:searchTerm% OR " +
            " d.description LIKE %:searchTerm% OR d.merchant_name LIKE %:searchTerm% OR " +
            " d.brand_names LIKE %:searchTerm% OR d.category_names LIKE %:searchTerm% OR " +
            " d.deal_code LIKE %:searchTerm% )", nativeQuery = true)
    Page<DealSearchIndex> findAllPendingDealsBySearchTerm(Pageable pageable, String searchTerm, String userType);

    /**
     * This method finds all not deleted pending deals.
     *
     * @param pageable       pageable
     * @param approvalStatus PENDING
     * @return dealSearchIndex page
     */
    Page<DealSearchIndex> findAllByApprovalStatusAndDealSourceAndIsDeletedFalse(Pageable pageable,
                                                                                ApprovalStatus approvalStatus,
                                                                                UserType userType);


    /**
     * This method find all active and approved deals for categoryId, active merchantId and searchTerm.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param categoryId   category id
     * @param searchTerm   search term
     * @param currentMills current Milliseconds
     * @return DealSearchIndex page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE + " AND "
            + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT + " AND " + RELATED_CATEGORIES + " AND " + SEARCH)
    Page<DealSearchIndex> findAllActiveDealsByActiveMerchantIdCategoryIdSearchTerm(Pageable pageable,
                                                                                   @Param("merchantId") String merchantId,
                                                                                   @Param("categoryId") String categoryId,
                                                                                   @Param("searchTerm") String searchTerm,
                                                                                   @Param("currentMills") long currentMills,
                                                                                   @Param("dealSource") UserType dealSource);

    /**
     * This method find all active and approved deals for active merchantId and searchTerm.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param searchTerm   search term
     * @param currentMills current Milliseconds
     * @return DealSearchIndex page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE + " AND "
            + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT + " AND " + SEARCH)
    Page<DealSearchIndex> findAllActiveDealsByActiveMerchantIdAndSearchTerm(Pageable pageable,
                                                                            @Param("merchantId") String merchantId,
                                                                            @Param("searchTerm") String searchTerm,
                                                                            @Param("currentMills") long currentMills,
                                                                            @Param("dealSource") UserType dealSource);

    /**
     * This method find all active and approved deals for active merchantId and categoryId.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param categoryId   category id
     * @param currentMills current Milliseconds
     * @return DealSearchIndex page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE + " AND "
            + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT + " AND " + RELATED_CATEGORIES)
    Page<DealSearchIndex> findAllActiveDealsByActiveMerchantIdAndCategoryId(Pageable pageable,
                                                                            @Param("merchantId") String merchantId,
                                                                            @Param("categoryId") String categoryId,
                                                                            @Param("currentMills") long currentMills,
                                                                            @Param("dealSource") UserType dealSource);

    /**
     * This method find all active and approved deals for categoryId and searchTerm.
     *
     * @param pageable     pageable
     * @param categoryId   category id
     * @param searchTerm   search term
     * @param currentMills current Milliseconds
     * @return DealSearchIndex page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT + " AND " + DEAL_SOURCE + " AND "
            + RELATED_CATEGORIES + " AND " + SEARCH)
    Page<DealSearchIndex> findAllActiveDealsCategoryIdAndSearchTermAndActiveMerchant(Pageable pageable,
                                                                                     @Param("categoryId") String categoryId,
                                                                                     @Param("searchTerm") String searchTerm,
                                                                                     @Param("currentMills") long currentMills,
                                                                                     @Param("dealSource") UserType dealSource);

    /**
     * This method find all active and approved deals for categoryId.
     *
     * @param pageable     pageable
     * @param categoryId   category id
     * @param currentMills current Milliseconds
     * @return DealSearchIndex page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT + " AND " + DEAL_SOURCE + " AND "
            + RELATED_CATEGORIES)
    Page<DealSearchIndex> findAllActiveDealsByCategoryIdAndActiveMerchant(Pageable pageable,
                                                                          @Param("categoryId") String categoryId,
                                                                          @Param("currentMills") long currentMills,
                                                                          @Param("dealSource") UserType dealSource);

    /**
     * This method find all active and approved deals for active merchantId.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param currentMills current Milliseconds
     * @return DealSearchIndex page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + MERCHANT_ID + " AND " + DEAL_SOURCE + " AND "
            + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT)
    Page<DealSearchIndex> findAllActiveDealsByActiveMerchantId(Pageable pageable,
                                                               String merchantId,
                                                               @Param("currentMills") long currentMills,
                                                               @Param("dealSource") UserType dealSource);

    /**
     * This method will return all active deals for active merchants by category id, merchant id, search term and order by expiryOn.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param categoryId   category id
     * @param searchTerm   search term
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds from DealSearchIndex ds WHERE ds.merchantId=:merchantId AND ds.dealSource=:userType AND "
            + VALID_ACTIVE_DEALS + " AND ds.relatedCategories LIKE %:categoryId% AND " +
            "( ds.title LIKE %:searchTerm% OR ds.subTitle LIKE %:searchTerm% OR ds.description LIKE %:searchTerm% " +
            "OR ds.merchantName LIKE %:searchTerm% OR ds.brandNames LIKE %:searchTerm% OR " +
            " ds.categoryNames LIKE %:searchTerm% OR ds.dealCode LIKE %:searchTerm% )")
    Page<DealSearchIndex> findAllValidDealsByMerchantIdCategoryIdSearchTerm(Pageable pageable,
                                                                            @Param("merchantId") String merchantId,
                                                                            @Param("categoryId") String categoryId,
                                                                            @Param("searchTerm") String searchTerm,
                                                                            @Param("currentMills") long currentMills,
                                                                            @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants by, merchant id, search term and order by expiryOn.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param searchTerm   search term
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds from DealSearchIndex ds WHERE ds.merchantId=:merchantId AND ds.dealSource=:userType AND " +
            VALID_ACTIVE_DEALS + " AND ( ds.title LIKE %:searchTerm% OR ds.subTitle LIKE %:searchTerm% " +
            "OR ds.description LIKE %:searchTerm% OR ds.merchantName LIKE %:searchTerm% OR ds.brandNames " +
            "LIKE %:searchTerm% OR ds.categoryNames LIKE %:searchTerm% OR ds.dealCode LIKE %:searchTerm% )")
    Page<DealSearchIndex> findAllValidDealsByMerchantIdAndSearchTerm(Pageable pageable,
                                                                     @Param("merchantId") String merchantId,
                                                                     @Param("searchTerm") String searchTerm,
                                                                     @Param("currentMills") long currentMills,
                                                                     @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants by, merchant id, category id and order by expiryOn.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param categoryId   category id
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE ds.merchantId=:merchantId AND ds.dealSource=:userType AND " +
            VALID_ACTIVE_DEALS + " AND ds.relatedCategories LIKE %:categoryId%")
    Page<DealSearchIndex> findAllValidDealsByMerchantIdAndCategoryId(Pageable pageable,
                                                                     @Param("merchantId") String merchantId,
                                                                     @Param("categoryId") String categoryId,
                                                                     @Param("currentMills") long currentMills,
                                                                     @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants by category id, search term and order by expiryOn.
     *
     * @param pageable     pageable
     * @param categoryId   category id
     * @param searchTerm   search term
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds from DealSearchIndex ds WHERE ds.dealSource=:userType AND " + VALID_ACTIVE_DEALS
            + " AND ds.relatedCategories LIKE %:categoryId% AND ( ds.title LIKE %:searchTerm% OR ds.subTitle " +
            "LIKE %:searchTerm% OR ds.brandNames LIKE %:searchTerm% OR ds.categoryNames LIKE %:searchTerm% " +
            " OR ds.dealCode LIKE %:searchTerm% )")
    Page<DealSearchIndex> findAllValidDealsByCategoryIdAndSearchTerm(Pageable pageable,
                                                                     @Param("categoryId") String categoryId,
                                                                     @Param("searchTerm") String searchTerm,
                                                                     @Param("currentMills") long currentMills,
                                                                     @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants by category id and order by expiryOn.
     *
     * @param pageable     pageable
     * @param categoryId   category id
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE ds.dealSource=:userType AND " + VALID_ACTIVE_DEALS
            + " AND ds.relatedCategories LIKE %:categoryId%")
    Page<DealSearchIndex> findAllValidDealsByCategoryId(Pageable pageable,
                                                        @Param("categoryId") String categoryId,
                                                        @Param("currentMills") long currentMills,
                                                        @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants by merchant id and order by expiryOn.
     *
     * @param pageable     pageable
     * @param merchantId   merchant id
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE ds.dealSource=:userType AND " + VALID_ACTIVE_DEALS + " AND ds.merchantId=:merchantId")
    Page<DealSearchIndex> findAllValidDealsByMerchantId(Pageable pageable,
                                                        String merchantId,
                                                        @Param("currentMills") long currentMills,
                                                        @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants by search term and order by expiryOn.
     *
     * @param pageable     pageable
     * @param searchTerm   search term
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds from DealSearchIndex ds WHERE ds.dealSource=:userType AND " + VALID_ACTIVE_DEALS
            + " AND (ds.title LIKE %:searchTerm% OR ds.subTitle LIKE %:searchTerm% OR ds.description" +
            " LIKE %:searchTerm% OR ds.merchantName LIKE %:searchTerm% OR ds.brandNames " +
            "LIKE %:searchTerm% OR ds.categoryNames LIKE %:searchTerm% OR ds.dealCode LIKE %:searchTerm% )")
    Page<DealSearchIndex> findAllValidDealsBySearchTerm(Pageable pageable,
                                                        @Param("searchTerm") String searchTerm,
                                                        @Param("currentMills") long currentMills,
                                                        @Param("userType") UserType userType);

    /**
     * This method will return all active deals for active merchants order by expiryOn.
     *
     * @param pageable     pageable
     * @param currentMills current milliSeconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE ds.dealSource=:userType AND " + VALID_ACTIVE_DEALS)
    Page<DealSearchIndex> findAllValidDeals(Pageable pageable, @Param("currentMills") long currentMills,
                                            @Param("userType") UserType userType);

    /**
     * This method return randomly selected 10 deals which not expired yet for merchant.
     *
     * @param currentMills currentMills - current time in milliseconds
     * @return deal list
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.is_deleted = false AND d.deal_source='MERCHANT'" +
            " AND d.approval_status= 'APPROVED' AND d.expired_on > ?1 AND d.valid_from <= ?1" +
            " AND d.is_active_merchant = true ORDER BY RAND() LIMIT ?2",
            nativeQuery = true)
    List<DealSearchIndex> getRandomDealsForMerchant(long currentMills, int dealsOfTheDayLimit);

    /**
     * This method return randomly selected 10 deals which not expired yet for bank.
     *
     * @param currentMills       currentMills - current time in milliseconds
     * @param dealsOfTheDayLimit dealsOfTheDaySize
     * @return deal List
     */
    @Query(value = "SELECT * FROM search_deal d WHERE d.is_deleted = false AND d.deal_source='BANK' " +
            "AND d.approval_status= 'APPROVED' AND d.expired_on > ?1 AND d.valid_from <= ?1 AND " +
            "d.is_active_merchant = true ORDER BY RAND() LIMIT ?2",
            nativeQuery = true)
    List<DealSearchIndex> getRandomDealsForBank(long currentMills, int dealsOfTheDayLimit);

    /**
     * This method returns active and valid deal count for a specific category id.
     *
     * @param currentMills current milliseconds
     * @param categoryId   category id
     * @return active and valid deal count
     */
    @Query(value = "SELECT COUNT(ds) FROM DealSearchIndex ds WHERE " + VALID_ACTIVE_DEALS
            + " AND ds.relatedCategories LIKE %:categoryId%")
    long getActiveValidDealsCountByCategoryId(@Param("currentMills") long currentMills, String categoryId);

    /**
     * This method returns active and valid deal count for a specific brand id.
     *
     * @param currentMills current milliseconds
     * @param brandId      brand id
     * @return active and valid deal count
     */
    @Query(value = "SELECT COUNT(ds) FROM DealSearchIndex ds WHERE " + VALID_ACTIVE_DEALS
            + " AND ds.relatedBrands LIKE %:brandId%")
    long getActiveValidDealsCountByBrandId(@Param("currentMills") long currentMills, String brandId);

    /**
     * This method finds all active and approved deals by search.
     *
     * @param pageable     pagebale
     * @param searchTerm   search term
     * @param currentMills current Milliseconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT + " AND "
            + DEAL_SOURCE + " AND " + SEARCH)
    Page<DealSearchIndex> findAllApprovedActiveDealsBySearchTermAndActiveMerchant(Pageable pageable,
                                                                                  @Param("searchTerm") String searchTerm,
                                                                                  @Param("currentMills") long currentMills,
                                                                                  @Param("dealSource") UserType dealSource);

    /**
     * This method finds all active and approved deals.
     *
     * @param pageable     pageable
     * @param currentMills current milliseconds
     * @return DealSearchIndex Page
     */
    @Query(value = "SELECT ds FROM DealSearchIndex ds WHERE " + DEAL_SOURCE + " AND " + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT)
    Page<DealSearchIndex> findAllActiveDealsWithActiveMerchant(Pageable pageable,
                                                               @Param("currentMills") long currentMills,
                                                               @Param("dealSource") UserType dealSource);

    /**
     * This method is used to get total number of active deals.
     *
     * @param currentMills currentMills
     * @return total number of active deals.
     */
    @Query("SELECT COUNT(ds) FROM DealSearchIndex ds WHERE " + VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT)
    int getTotalNumberOfActiveDeals(@Param("currentMills") long currentMills);

    /**
     * This method is used to get total number of active deals for merchantId.
     *
     * @param merchantId   merchantId
     * @param currentMills currentMills
     * @return total number of active deals.
     */
    @Query("SELECT COUNT(ds) FROM DealSearchIndex ds WHERE " + MERCHANT_ID + " AND " +
            VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT)
    int getTotalNumberOfActiveDealsForMerchantId(@Param("merchantId") String merchantId,
                                                 @Param("currentMills") long currentMills);

    /**
     * This method will return DealSearchIndex list by merchant ids
     *
     * @param merchantIds merchantIds List
     * @return DealSearchIndex List
     */
    List<DealSearchIndex> getAllByMerchantIdIn(List<String> merchantIds);

    /**
     * This method find all active deal ids for active userId.
     *
     * @param merchantId   merchant id
     * @param currentMills current Milliseconds
     * @return dealId list.
     */
    @Query(value = "SELECT ds.id FROM DealSearchIndex ds WHERE " + MERCHANT_ID + " AND " +
            VALID_ACTIVE_DEALS_FOR_ACTIVE_MERCHANT)
    List<String> getActiveDealsByActiveBankId(String merchantId, long currentMills);

    /**
     * This method returns a count of deals for specific category id
     *
     * @param categoryId category id
     * @return count deals Count By CategoryId
     */
    long countByRelatedCategoriesContainsAndIsDeletedFalse(String categoryId);

    /**
     * This method return a count of deal by brand id
     *
     * @param brandId brand id
     * @return count deals Count By BrandId
     */
    long countByRelatedBrandsContainsAndIsDeletedFalse(String brandId);
}