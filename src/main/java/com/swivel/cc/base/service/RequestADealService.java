package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.CombinedDealRequest;
import com.swivel.cc.base.domain.entity.RequestADeal;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidRequestADealException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.RequestADealRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RequestADealService {

    protected static final String ALL = "ALL";
    private static final String INVALID_REQUEST_A_DEAL = "Invalid request a deal Id: ";
    private final RequestADealRepository requestADealRepository;
    private final RequestADealSearchIndexService requestADealSearchIndexService;


    @Autowired
    public RequestADealService(RequestADealRepository requestADealRepository,
                               RequestADealSearchIndexService requestADealSearchIndexService) {
        this.requestADealRepository = requestADealRepository;
        this.requestADealSearchIndexService = requestADealSearchIndexService;
    }

    /**
     * Save new request a deal into database
     *
     * @param requestADeal RequestDeal
     * @return RequestDeal
     */
    public RequestADeal saveRequestADeal(RequestADeal requestADeal, BusinessMerchantResponseDto merchant, UserType userType) {
        try {
            if ((userType == UserType.BANK)) {
                requestADeal.setToUserType(UserType.BANK);
            } else {
                requestADeal.setToUserType(UserType.MERCHANT);
            }
            var savedRequestADeal = requestADealRepository.save(requestADeal);
            requestADealSearchIndexService.save(savedRequestADeal, merchant.getBusinessName());
            return savedRequestADeal;
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving request deal into database was failed.", e);
        }
    }

    /**
     * Get request deals by user Id and toUserType.
     *
     * @param pageable pageable
     * @param userId   userId
     * @return page
     */
    public Page<RequestADeal> getRequestDealsByUserIdAndToUserType(Pageable pageable, String userId, UserType toUserType) {
        try {
            if (toUserType == UserType.BANK) {
                return requestADealRepository.findAllByUserIdAndToUserType(pageable, userId, UserType.BANK);
            } else {
                return requestADealRepository.findAllByUserIdAndToUserType(pageable, userId, UserType.MERCHANT);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading request a deal by user id from database was failed", e);
        }
    }

    /**
     * Get request deal by id
     *
     * @param id requestDeal id
     * @return requestDeal
     */
    public RequestADeal getRequestADealById(String id) {
        try {
            Optional<RequestADeal> optionalRequestADeal = requestADealRepository.findById(id);
            if (optionalRequestADeal.isPresent()) {
                return optionalRequestADeal.get();
            } else {
                throw new InvalidRequestADealException(INVALID_REQUEST_A_DEAL + id);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading request a deal by id from database was failed", e);
        }
    }

    /**
     * Get Combined Deal Requests
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return page CombinedDealRequest
     */
    public Page<CombinedDealRequest> getAllCombinedDealRequests(Pageable pageable,
                                                                String merchantId, String searchTerm, UserType userType) {
        try {

            return requestADealSearchIndexService
                    .getAllCombinedDealRequests(pageable, merchantId, searchTerm, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("fetching all combinedDealRequest from database was failed.", e);
        }
    }

    /**
     * This method returns count of combined deal request by merchant, category, brand.
     *
     * @param merchantId merchant id
     * @param category   category
     * @param brand      brand
     * @param toUserType toUserType
     * @return count
     */
    public long getCountForCombinedRequestADeal(String merchantId,
                                                Category category, Brand brand, UserType toUserType) {
        try {
            return (brand != null) ?
                    requestADealRepository
                            .countRequestADealsByMerchantIdAndCategoryAndBrandAndToUserType(merchantId,
                                    category, brand, toUserType)
                    : requestADealRepository
                    .countRequestADealsByMerchantIdAndCategoryAndBrandAndToUserType(merchantId,
                            category, null, toUserType);
        } catch (DataAccessException e) {
            throw new QponCoreException("returning count for the combined request a deal from database was failed.", e);
        }
    }

    /**
     * Get Combined Deal Requests by category id, brand id and merchant id
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param categoryId category id
     * @param brandId    brand id
     * @return RequestADeal List
     */
    public Page<RequestADeal> getAllRequestADealDetail(Pageable pageable, String merchantId,
                                                       String categoryId, String brandId, UserType userType) {
        try {
            return ((brandId != null) ?
                    requestADealRepository
                            .getAllByMerchantIdAndBrandIdCategoryIdAndUserType(pageable,
                                    merchantId, brandId, categoryId, userType)
                    : requestADealRepository
                    .getAllByMerchantIdCategoryIdAndUserType(pageable, merchantId, categoryId, userType));
        } catch (DataAccessException e) {
            throw new QponCoreException("fetching combined request a deal note list from database was failed.", e);
        }
    }

}
