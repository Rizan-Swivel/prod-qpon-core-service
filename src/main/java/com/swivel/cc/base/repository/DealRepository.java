package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface DealRepository extends JpaRepository<Deal, String> {

    /**
     * This method find the deal by given Id which not deleted and approval status pending.
     *
     * @param id deal Id
     * @return optional Deal
     */
    @Query(value = "SELECT * FROM deal d WHERE d.id=?1 AND d.is_deleted = false AND d.approval_status= 'PENDING'",
            nativeQuery = true)
    Optional<Deal> findPendingDealById(String id);

    /**
     * This method finds all non deleted deals.
     *
     * @param pageable pageable
     * @return deal page
     */
    @Query(value = "SELECT * FROM deal d WHERE d.is_deleted = false", nativeQuery = true)
    Page<Deal> findAllDeals(Pageable pageable);

    /**
     * This method find the deal by given Id which not deleted.
     *
     * @param id deal Id
     * @return optional Deal
     */
    @Query(value = "SELECT * FROM deal d WHERE d.id=?1 AND d.is_deleted = false",
            nativeQuery = true)
    Optional<Deal> findDealById(String id);


    /**
     * This method finds all deals for given merchant Id.
     *
     * @param pageable   pageable
     * @param merchantId merchantId
     * @return deal page
     */
    @Query(value = "SELECT * FROM deal d WHERE d.merchant_id=?1 AND d.is_deleted = false",
            nativeQuery = true)
    Page<Deal> findAllDealsByMerchantId(Pageable pageable, String merchantId);

    /**
     * This method return a count of deal by brand id
     *
     * @param brandId brand id
     * @return count deals Count By BrandId
     */
    long countByRelatedBrandsId(String brandId);

    /**
     * This method returns a count of deals for specific category id
     *
     * @param categoryId category id
     * @return count deals Count By CategoryId
     */
    long countByRelatedCategoriesId(String categoryId);

    /**
     * This method is used to get total number of new deals.
     *
     * @param dateInMillis dateInMillis
     * @return total number of new deals
     */
    int countByCreatedAtGreaterThanEqual(long dateInMillis);

    /**
     * This method is used to get total number of expiring deals.
     *
     * @param startTimestamp startTimestamp
     * @param endTimestamp   endTimestamp
     * @return total number of expiring deals
     */
    int countByExpiredOnGreaterThanEqualAndExpiredOnLessThan(long startTimestamp, long endTimestamp);

    /**
     * This method is used to get total number of new deals for merchantId.
     *
     * @param merchantId   merchantId
     * @param dateInMillis dateInMillis
     * @return total number of new deals
     */
    int countByMerchantIdAndCreatedAtGreaterThanEqual(String merchantId, long dateInMillis);

    /**
     * This method is used to get total number of expiring deals for merchantId.
     *
     * @param merchantId     merchantId
     * @param startTimestamp startTimestamp
     * @param endTimestamp   endTimestamp
     * @return total number of expiring deals
     */
    int countByMerchantIdAndExpiredOnGreaterThanEqualAndExpiredOnLessThan(String merchantId, long startTimestamp,
                                                                          long endTimestamp);

    /**
     * This method is used to get total number of deals for merchantId.
     *
     * @param merchantId merchantId
     * @return total number of deals
     */
    int countByMerchantId(String merchantId);
}