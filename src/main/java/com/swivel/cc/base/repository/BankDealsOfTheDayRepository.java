package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.BankDeal;
import com.swivel.cc.base.domain.entity.BankDealsOfTheDay;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;

/**
 * Bank Deals of the date repository.
 */
public interface BankDealsOfTheDayRepository extends JpaRepository<BankDealsOfTheDay, String> {

    /**
     * Update all active deals of the days to false
     */
    @Modifying
    @Transactional
    @Query(value = "UPDATE BankDealsOfTheDay SET isActive = false WHERE isActive = true ")
    void disableActiveDeals();

    /**
     * This method will find deals of the day by applied date.
     *
     * @param date date
     * @return deals of the day.
     */
    @Query("SELECT dod.bankDeal FROM BankDealsOfTheDay dod WHERE dod.applyOn = :date")
    List<BankDeal> getDealByAppliedOn(@Param("date") Date date);

    /**
     * This method is used to get deals of the day by date & search term.
     *
     * @param searchTerm searchTerm
     * @param date       date
     * @return deals of the day.
     */
    @Query("SELECT dod.bankDeal FROM BankDealsOfTheDay dod WHERE dod.applyOn = :date AND dod.bankDeal IN " +
            " (SELECT d.id FROM DealSearchIndex d WHERE (d.title LIKE %:searchTerm% OR " +
            " d.subTitle LIKE %:searchTerm% OR d.description LIKE %:searchTerm% OR " +
            " d.merchantName LIKE %:searchTerm% OR d.brandNames LIKE %:searchTerm% OR " +
            " d.categoryNames LIKE %:searchTerm% ))")
    List<BankDeal> getDealsOfTheDayByDateAndSearchTerm(@Param("searchTerm") String searchTerm,
                                                       @Param("date") Date date);
}