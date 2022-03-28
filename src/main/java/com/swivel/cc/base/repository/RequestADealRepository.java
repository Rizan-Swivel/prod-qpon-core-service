package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.RequestADeal;
import com.swivel.cc.base.enums.UserType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface RequestADealRepository extends JpaRepository<RequestADeal, String> {

    /**
     * This query returns all request deals by user Id
     *
     * @param pageable pageable
     * @param userId   userId
     * @return page
     */
    Page<RequestADeal> findAllByUserIdAndToUserType(Pageable pageable, String userId, UserType userType);

    /**
     * This query returns count of combined deal request by merchant, category, brand.
     *
     * @param merchantId merchant id
     * @param category   category
     * @param brand      brand
     * @return count
     */
    long countRequestADealsByMerchantIdAndCategoryAndBrandAndToUserType(String merchantId,
                                                                        Category category, Brand brand, UserType userType);

    /**
     * This query returns all request a deal by merchant id, brand id & category id
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param brandId    brand id
     * @param categoryId category id
     * @return RequestADeal List
     */
    @Query(value = "SELECT rd FROM RequestADeal rd where rd.toUserType=:userType and rd.merchantId=:merchantId " +
            "AND rd.brand.id=:brandId AND rd.category.id=:categoryId")
    Page<RequestADeal> getAllByMerchantIdAndBrandIdCategoryIdAndUserType(Pageable pageable, String merchantId,
                                                                         String brandId, String categoryId, UserType userType);

    /**
     * This query returns all request a deal by merchant id, category id
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param categoryId category id
     * @return RequestADeal List
     */
    @Query(value = "SELECT rd FROM RequestADeal rd where rd.toUserType=:userType and rd.merchantId=:merchantId " +
            "AND rd.brand.id is null AND rd.category.id=:categoryId")
    Page<RequestADeal> getAllByMerchantIdCategoryIdAndUserType(Pageable pageable, String merchantId, String categoryId, UserType userType);

    /**
     * This method is used to get total number of new deal request.
     *
     * @param timestamp timestamp
     * @return total number of new deal request
     */
    long countByCreatedAtGreaterThanEqual(long timestamp);

    /**
     * This method is used to get total number of new deal request for userId.
     *
     * @param userId userId
     * @param timestamp  timestamp
     * @return total number of new deal request
     */
    long countByMerchantIdAndCreatedAtGreaterThanEqual(String userId, long timestamp);

    /**
     * This method is used to get total number of deal requests for userId.
     *
     * @param userId userId
     * @return total number of deal requests
     */
    long countByMerchantId(String userId);
}
