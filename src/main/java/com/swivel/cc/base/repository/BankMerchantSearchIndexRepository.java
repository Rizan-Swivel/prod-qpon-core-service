package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.BankMerchantSearchIndex;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface BankMerchantSearchIndexRepository extends JpaRepository<BankMerchantSearchIndex, String> {

    /**
     * This method return all BankMerchantSearchIndex by bank id and search term.
     *
     * @param pageable   pageable
     * @param bankId     bank id
     * @param searchTerm search term
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByBankIdAndMerchantNameContaining(Pageable pageable, String bankId,
                                                                           String searchTerm);

    /**
     * This method return all BankMerchantSearchIndex by bank id, search term and active merchant true.
     *
     * @param pageable   pageable
     * @param bankId     bank id
     * @param searchTerm search term
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByBankIdAndIsActiveMerchantTrueAndMerchantNameContaining(Pageable pageable,
                                                                                                  String bankId,
                                                                                                  String searchTerm);

    /**
     * This method return all BankMerchantSearchIndex by bank id.
     *
     * @param pageable pageable
     * @param bankId   bank id
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByBankId(Pageable pageable, String bankId);

    /**
     * This method return all BankMerchantSearchIndex by bank id and activeMerchant true.
     *
     * @param pageable pageable
     * @param bankId   bank id
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByBankIdAndIsActiveMerchantTrue(Pageable pageable, String bankId);

    /**
     * This method return all BankMerchantSearchIndex by merchant id.
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByMerchantId(Pageable pageable, String merchantId);

    /**
     * This method return all BankMerchantSearchIndex by merchant id and isActiveBank true.
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByMerchantIdAndIsActiveBankTrue(Pageable pageable, String merchantId);

    /**
     * This method return all BankMerchantSearchIndex by merchant id and search term.
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByMerchantIdAndBankNameContaining(Pageable pageable, String merchantId,
                                                                           String searchTerm);


    /**
     * This method return all BankMerchantSearchIndex by merchant id, search term and isActiveBank true.
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return BankMerchantSearchIndex Page
     */
    Page<BankMerchantSearchIndex> findAllByMerchantIdAndBankNameContainingAndIsActiveBankTrue(Pageable pageable,
                                                                                              String merchantId,
                                                                                              String searchTerm);

    /**
     * This method delete BankMerchantSearchIndex by deal id.
     *
     * @param dealId deal id
     */
    void deleteByFromLatestDealId(String dealId);

    /**
     * This method will return all dealIds.
     *
     * @return dealId page.
     */
    @Query("SELECT b.fromLatestDealId FROM BankMerchantSearchIndex b")
    Page<String> getAllDealIds(Pageable pageable);

    /**
     * This method is used to get distinct bankIds.
     * List is used as return type instead of page, assuming there won't be a situation
     * where bankIds are more than 250.
     *
     * @return distinct bank id list.
     */
    @Query("SELECT DISTINCT (b.bankId) FROM BankMerchantSearchIndex b")
    List<String> getAllDistinctBankIds();

    /**
     * This method is used to get distinct merchantIds.
     *
     * @return distinct merchantId page.
     */
    @Query("SELECT DISTINCT b.merchantId FROM BankMerchantSearchIndex b")
    Page<String> getAllDistinctMerchantIds(Pageable pageable);

    /**
     * This method will delete records with the list of bankIds.
     *
     * @param bankIds list of bankIds.
     */
    void deleteByBankIdIn(List<String> bankIds);

    /**
     * This method will delete records with the list of merchantIds.
     *
     * @param merchantIds list of merchantIds.
     */
    void deleteByMerchantIdIn(List<String> merchantIds);
}
