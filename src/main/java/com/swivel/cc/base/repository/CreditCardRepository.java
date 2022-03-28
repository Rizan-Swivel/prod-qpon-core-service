package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.CombinedCreditCardRequest;
import com.swivel.cc.base.domain.entity.CreditCardRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CreditCardRepository extends JpaRepository<CreditCardRequest, String> {

    String SEARCH_IN_ALL_FIELDS = " cc.fullName like %:searchTerm% or cc.mobileNumber like %:searchTerm% or" +
            " cc.email like %:searchTerm% or cc.nic like %:searchTerm% or cc.city like %:searchTerm% or " +
            "cc.companyName like %:searchTerm% or cc.profession like %:searchTerm%";
    String GROUP_BY_BANK = "group by cc.bankId";
    String BANK_ID = "cc.bankId=:bankId ";

    /**
     * This method returns credit card request page for bank and search term.
     *
     * @param pageable   pageable
     * @param bankId     bank id
     * @param searchTerm search term
     * @return Page<CreditCardRequest>
     */
    @Query("select cc from CreditCardRequest cc where cc.bankId=:bankId and" + SEARCH_IN_ALL_FIELDS)
    Page<CreditCardRequest> findAllByBankIdWithSearch(Pageable pageable, String bankId, String searchTerm);

    /**
     * This method returns credit card request page for bank.
     *
     * @param pageable pageable
     * @param bankId   bank id
     * @return Page<CreditCardRequest>
     */
    Page<CreditCardRequest> findAllByBankId(Pageable pageable, String bankId);


    /**
     * This method returns credit card request page.
     *
     * @param pageable   pageable
     * @param searchTerm search term
     * @return Page<CreditCardRequest>
     */
    @Query("select cc from CreditCardRequest cc where " + SEARCH_IN_ALL_FIELDS)
    Page<CreditCardRequest> findAllBySearchTerm(Pageable pageable, String searchTerm);

    /**
     * This method returns all the credit card request by user.
     *
     * @param userId user id
     * @return List<CreditCardRequest>
     */
    Page<CreditCardRequest> findAllByUserId(Pageable pageable, String userId);

    @Query("select new CombinedCreditCardRequest(cc.bankId, count(cc)) from CreditCardRequest cc " + GROUP_BY_BANK)
    Page<CombinedCreditCardRequest> getAllGroupCreditCardRequests(Pageable pageable);

    @Query("select new CombinedCreditCardRequest(cc.bankId, count(cc)) from CreditCardRequest cc where"
            + SEARCH_IN_ALL_FIELDS + "  " + GROUP_BY_BANK)
    Page<CombinedCreditCardRequest> getAllGroupCreditCardRequestBySearchTerm(Pageable pageable, String searchTerm);

    @Query("select new CombinedCreditCardRequest(cc.bankId, count(cc)) from CreditCardRequest cc where " +
            BANK_ID + " " + GROUP_BY_BANK)
    Page<CombinedCreditCardRequest> getAllGroupCreditCardRequestByMerchantId(Pageable pageable, String bankId);

    @Query("select new CombinedCreditCardRequest(cc.bankId, count(cc)) from CreditCardRequest cc where " + BANK_ID + " and"
            + SEARCH_IN_ALL_FIELDS + " " + GROUP_BY_BANK)
    Page<CombinedCreditCardRequest> getAllGroupCreditCardRequestByMerchantIdAndSearchTerm(Pageable pageable,
                                                                                          String bankId,
                                                                                          String searchTerm);

    /**
     * This method is used to get total number of new CC request.
     *
     * @param timestamp timestamp
     * @return total number of new credit card request
     */
    long countByCreatedAtGreaterThanEqual(long timestamp);

    /**
     * This method is used to get total number of new CC request for bankId.
     *
     * @param bankId       bankId
     * @param dateInMillis dateInMillis
     * @return total number of credit card request.
     */
    Long countByBankIdAndCreatedAtGreaterThanEqual(String bankId, long dateInMillis);

    /**
     * This method is used to get total count of credit card request.
     *
     * @param bankId bankId
     * @return total count of credit card request.
     */
    long countAllByBankId(String bankId);
}
