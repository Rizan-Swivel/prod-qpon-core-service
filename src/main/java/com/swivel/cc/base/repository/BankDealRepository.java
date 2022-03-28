package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.Deal;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * Bank deal repository
 */
public interface BankDealRepository extends JpaRepository<BankDeal, String> {

    /**
     * This method find the deal by given Id which not deleted.
     *
     * @param id bank deal id
     * @return optional Deal
     */
    @Query(value = "SELECT d FROM BankDeal d WHERE d.id=?1 AND d.isDeleted = false")
    Optional<BankDeal> findDealById(String id);

    /**
     * This method find the deal by given Id which not deleted and approval status pending.
     *
     * @param id deal Id
     * @return optional Deal
     */
    @Query(value = "SELECT d FROM BankDeal d WHERE d.id=?1 AND d.isDeleted = false AND d.approvalStatus= 'PENDING'")
    Optional<Deal> findPendingDealById(String id);

    /**
     * This method is used to get total number of new deals for bankId.
     *
     * @param bankId       bankId
     * @param dateInMillis dateInMillis
     * @return total number of new deals
     */
    long countByBankIdAndCreatedAtGreaterThanEqual(String bankId, long dateInMillis);

    /**
     * This method is used to get total number of expiring deals for bankId.
     *
     * @param bankId         bankId
     * @param startTimestamp startTimestamp
     * @param endTimestamp   endTimestamp
     * @return total number of expiring deals
     */
    long countByBankIdAndExpiredOnGreaterThanEqualAndExpiredOnLessThan(String bankId, long startTimestamp,
                                                                       long endTimestamp);

    /**
     * This method is used to get total number of deals for bankId.
     *
     * @param bankId bankId
     * @return total number of deals
     */
    long countByBankId(String bankId);

    /**
     * This method is used to get total number of new deals.
     *
     * @param dateInMillis dateInMillis
     * @return total number of new deals
     */
    long countByCreatedAtGreaterThanEqual(long dateInMillis);

    /**
     * This method is used to get total number of expiring deals.
     *
     * @param startTimestamp startTimestamp
     * @param endTimestamp   endTimestamp
     * @return total number of expiring deals
     */
    long countByExpiredOnGreaterThanEqualAndExpiredOnLessThan(long startTimestamp, long endTimestamp);

    /**
     * This method is used to get active bank deals by merchantId.
     *
     * @param pageable   pageable
     * @param merchantId merchant Id
     * @param dealIds    dealIds
     * @return bank deal page.
     */
    Page<BankDeal> findByMerchantIdAndIdIn(Pageable pageable, String merchantId, List<String> dealIds);

    /**
     * This method returns true when deal's expiredOn is equal or less than to currentMillieSecond.
     *
     * @param dealId           deal id
     * @param currentTimeStamp current time stamp in milliSecond
     * @return true/ false
     */
    @Query("SELECT CASE WHEN (bd.expiredOn < :currentTimeStamp OR bd.expiredOn = :currentTimeStamp)" +
            " THEN TRUE ELSE FALSE END FROM BankDeal bd WHERE bd.id=:dealId")
    boolean isBankDealExpired(String dealId, long currentTimeStamp);
}
