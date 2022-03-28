package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.CategoryBrandMerchant;
import com.swivel.cc.base.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

/**
 * This repository handles related categories for merchant
 */
public interface CategoryBrandMerchantRepository extends JpaRepository<CategoryBrandMerchant, String> {


    /**
     * This method return merchant Id list by category Id which not deleted and merchant status active.
     *
     * @param categoryId categoryId
     * @param isActive   isActive false give blocked merchants
     * @return merchant Id list
     */
    Page<CategoryBrandMerchant> findAllByCategoriesAndIsActiveMerchant(Pageable pageable, String categoryId, boolean isActive);

    /**
     * This method return CategoryForMerchant Page by category Id which not deleted and merchant status active.
     *
     * @param isActive isActive false give blocked merchants
     * @return merchant Id list
     */
    Page<CategoryBrandMerchant> findAllByIsActiveMerchant(Pageable pageable, boolean isActive);


    /**
     * This method return merchant Id list by category Id which not deleted.
     *
     * @param pageable   pageable
     * @param categoryId category id
     * @return CategoryBrandMerchant Page
     */
    Page<CategoryBrandMerchant> findAllByCategories(Pageable pageable, String categoryId);

    /**
     * This method return CategoryForMerchant Page by category Id which not deleted.
     *
     * @param pageable pageable
     * @return CategoryBrandMerchant Page
     */
    Page<CategoryBrandMerchant> findAll(Pageable pageable);


    /**
     * This method return CategoryForMerchant by merchant Id
     *
     * @param merchantId merchantId
     * @return CategoryBrandMerchant
     */
    Optional<CategoryBrandMerchant> findByMerchantId(String merchantId);

    /**
     * This method return number of merchants for specific brand
     *
     * @param brandId brandId
     * @return count merchants Count By BrandId
     */
    long countDistinctMerchantIdByBrandsAndUserType(String brandId, UserType userType);

    /**
     * This method returns count of merchant for specific category id
     *
     * @param categoryId category id
     * @return count merchants Count By CategoryId
     */
    long countDistinctMerchantIdByCategoriesAndUserType(String categoryId, UserType userType);


    /**
     * This method returns active merchant count for a specific category id.
     *
     * @param categoryId category id
     * @return active merchant count
     */
    long countDistinctMerchantIdByCategoriesAndIsActiveMerchantTrueAndUserType(String categoryId, UserType userType);

    /**
     * This method returns active merchant count for a specific brand id.
     *
     * @param brandId category id
     * @return active merchant count
     */
    long countDistinctMerchantIdByBrandsAndIsActiveMerchantTrueAndUserType(String brandId, UserType userType);

    /**
     * This method returns true when brand id mapped with categoryBrandMerchant.
     *
     * @param brandId brand id
     * @return true/ false
     */
    boolean existsByBrands(String brandId);

    /**
     * This method returns true when the category id is associate with a categoryBrandMerchant.
     *
     * @param categoryId category id
     * @return true/ false
     */
    boolean existsAllByCategories(String categoryId);

    /**
     * This method will return list of CategoryBrandMerchant by merchant ids
     *
     * @param merchantIds merchant id list
     * @return CategoryBrandMerchant list
     */
    List<CategoryBrandMerchant> getAllByMerchantIdIn(List<String> merchantIds);

}
