package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.CombinedDealRequest;
import com.swivel.cc.base.domain.entity.RequestADeal;
import com.swivel.cc.base.domain.entity.RequestADealSearchIndex;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.RequestADealSearchIndexRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * This class should replace from a search engine.
 */
@Service
public class RequestADealSearchIndexService {

    private static final String ALL = "ALL";
    private final RequestADealSearchIndexRepository requestADealSearchIndexRepository;

    @Autowired
    public RequestADealSearchIndexService(RequestADealSearchIndexRepository requestADealSearchIndexRepository) {
        this.requestADealSearchIndexRepository = requestADealSearchIndexRepository;
    }


    /**
     * This method will save a RequestADealSearchIndex
     *
     * @param requestADeal RequestADeal
     * @param merchantName merchantName
     */
    public void save(RequestADeal requestADeal, String merchantName) {

        try {
            RequestADealSearchIndex requestADealSearchIndex = new RequestADealSearchIndex(requestADeal, merchantName);
            requestADealSearchIndexRepository.save(requestADealSearchIndex);

        } catch (
                DataAccessException e) {
            throw new QponCoreException("Saving requestADealSearchIndex into database was failed.", e);
        }

    }

    /**
     * This method returns All combinedRequestADeals
     *
     * @param pageable   pageable
     * @param merchantId merchant id
     * @param searchTerm search term
     * @return CombinedDealRequest Page
     */

    public Page<CombinedDealRequest> getAllCombinedDealRequests(Pageable pageable,
                                                                String merchantId,
                                                                String searchTerm, UserType userType) {
        try {
            if (merchantId.equals(ALL) && !searchTerm.equals(ALL)) {
                return requestADealSearchIndexRepository.getAllRequestADealSearchByMerchantNameAndUserType(pageable, searchTerm, userType);
            } else if (merchantId.equals(ALL)) {
                return requestADealSearchIndexRepository.getAllRequestADealByUserType(pageable, userType);
            } else if (searchTerm.equals(ALL)) {
                return requestADealSearchIndexRepository.getRequestADealByMerchantIdAndUserType(pageable, merchantId, userType);
            } else {
                return requestADealSearchIndexRepository.getRequestADealSearchByMerchantNameAndUserType(pageable, merchantId,
                        searchTerm, userType);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("fetching combinedDealRequest from the database was failed.", e);
        }
    }

    /**
     * This method will update merchant info.
     *
     * @param merchantIds merchant ids List
     * @param merchantMap merchant id, MerchantBusinessResponseDto Map
     */
    public void updateMerchantInfo(List<String> merchantIds, Map<String, MerchantBusinessResponseDto> merchantMap) {
        try {
            var requestADealSearchIndexList =
                    requestADealSearchIndexRepository.getAllByMerchantIdIn(merchantIds);
            for (var requestADealSearchIndex : requestADealSearchIndexList) {
                var merchant = merchantMap.get(requestADealSearchIndex.getMerchantId());
                requestADealSearchIndex.setMerchantName(merchant.getName());
            }
            requestADealSearchIndexRepository.saveAll(requestADealSearchIndexList);
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating merchant info for requestADealSearchIndex was failed", e);
        }
    }
}
