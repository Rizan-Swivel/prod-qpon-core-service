package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.CategoryBrandMerchantIndex;
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
public interface CategoryBrandMerchantIndexRepository extends JpaRepository<CategoryBrandMerchantIndex, String> {

    /**
     * This method returns all indexes by filtering from category id and merchantName
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @param searchTerm searchTerm
     * @param userType   userType
     * @return page
     */
    @Query(value = "SELECT cbmi from CategoryBrandMerchantIndex cbmi WHERE cbmi.categoryIds LIKE %:categoryId% " +
            "AND cbmi.merchantName LIKE %:searchTerm% AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllByCategoryIdAndSearchTerm(Pageable pageable,
                                                                      @Param("categoryId") String categoryId,
                                                                      @Param("searchTerm") String searchTerm,
                                                                      @Param("userType") UserType userType);

    /**
     * This method returns all indexes by filtering from category Id
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @param userType   userType
     * @return page
     */
    @Query(value = "SELECT cbmi from CategoryBrandMerchantIndex cbmi WHERE cbmi.categoryIds LIKE %:categoryId% " +
            "AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllByCategoryId(Pageable pageable,
                                                         @Param("categoryId") String categoryId,
                                                         @Param("userType") UserType userType);


    /**
     * This method returns all indexes by filtering from brand Id and merchantName
     *
     * @param pageable   pageable
     * @param brandId    brandId
     * @param searchTerm searchTerm
     * @return page
     */
    @Query(value = "SELECT cbmi from CategoryBrandMerchantIndex cbmi" +
            " WHERE cbmi.brandIds LIKE %:brandId% AND cbmi.merchantName LIKE %:searchTerm% ")
    Page<CategoryBrandMerchantIndex> findAllByBrandIdAndSearchTerm(Pageable pageable,
                                                                   @Param("brandId") String brandId,
                                                                   @Param("searchTerm") String searchTerm);


    /**
     * This method find CategoryBrandMerchantIndex by merchantId
     *
     * @param merchantId merchantId
     * @return optional CategoryBrandMerchantIndex
     */
    Optional<CategoryBrandMerchantIndex> findByMerchantId(String merchantId);


    /**
     * This method returns all indexes by filtering from brand id
     *
     * @param pageable pageable
     * @param brandId  brandId
     * @return page CategoryBrandMerchantIndex
     */
    @Query(value = "SELECT cbmi from CategoryBrandMerchantIndex cbmi WHERE cbmi.brandIds LIKE %:brandId%")
    Page<CategoryBrandMerchantIndex> findAllByBrandId(Pageable pageable,
                                                      @Param("brandId") String brandId);

    /**
     * @param pageable   pageable
     * @param searchTerm search term
     * @param userType   userType
     * @return page CategoryBrandMerchantIndex
     */
    @Query(value = "SELECT cbmi from CategoryBrandMerchantIndex cbmi WHERE cbmi.merchantName LIKE %:searchTerm% " +
            "AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllBySearch(Pageable pageable, @Param("searchTerm") String searchTerm,
                                                     @Param("userType") UserType userType);


    /**
     * This method returns all indexes
     *
     * @param pageable pageable
     * @param userType userType
     * @return page CategoryBrandMerchantIndex
     */
    @Query(value = "SELECT cbmi from CategoryBrandMerchantIndex cbmi where cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllCategoryBrandMerchantIndex(Pageable pageable,
                                                                       @Param("userType") UserType userType);


    /**
     * This method returns all indexes by filtering from category Id and merchantName for mobile.
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @param searchTerm searchTerm
     * @param userType   userType
     * @return page CategoryBrandMerchantIndex
     */
    @Query(value = "SELECT cbmi FROM CategoryBrandMerchantIndex cbmi WHERE cbmi.isActiveMerchant=true AND" +
            " cbmi.categoryIds LIKE %:categoryId%  AND cbmi.merchantName LIKE %:searchTerm% AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllByCategoryIdAndSearchTermAndActiveMerchant(
            Pageable pageable, @Param("categoryId") String categoryId,
            @Param("searchTerm") String searchTerm, @Param("userType") UserType userType);


    /**
     * This method returns all indexes by filtering from category Id for mobile.
     *
     * @param pageable   pageable
     * @param categoryId categoryId
     * @param userType   userType
     * @return page
     */
    @Query(value = "SELECT cbmi FROM CategoryBrandMerchantIndex cbmi WHERE cbmi.isActiveMerchant=true " +
            "AND cbmi.categoryIds LIKE %:categoryId% AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllByCategoryIdAndActiveMerchant(Pageable pageable,
                                                                          @Param("categoryId") String categoryId,
                                                                          @Param("userType") UserType userType);

    /**
     * This method returns all index with search of merchant name.
     *
     * @param pageable   pageable
     * @param searchTerm search term
     * @param userType   userType
     * @return page CategoryBrandMerchantIndex
     */
    @Query(value = "SELECT cbmi FROM CategoryBrandMerchantIndex cbmi WHERE cbmi.isActiveMerchant=true" +
            " AND cbmi.merchantName LIKE %:searchTerm% AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllBySearchAndActiveMerchant(
            Pageable pageable, @Param("searchTerm") String searchTerm, @Param("userType") UserType userType);


    /**
     * This method returns all indexes
     *
     * @param pageable pageable
     * @param userType userType
     * @return page CategoryBrandMerchantIndex
     */
    @Query(value = "SELECT cbmi FROM CategoryBrandMerchantIndex cbmi WHERE cbmi.isActiveMerchant= true " +
            "AND cbmi.userType=:userType")
    Page<CategoryBrandMerchantIndex> findAllCategoryBrandMerchantIndexByActiveMerchant(
            Pageable pageable, @Param("userType") UserType userType);

    /**
     * This method will return all the indexes by merchant id
     *
     * @param merchantIds merchant id list
     * @return categoryBrandMerchantIndex list
     */
    List<CategoryBrandMerchantIndex> getAllByMerchantIdIn(List<String> merchantIds);
}
