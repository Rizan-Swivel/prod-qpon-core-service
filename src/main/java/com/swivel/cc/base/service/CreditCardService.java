package com.swivel.cc.base.service;


import com.swivel.cc.base.domain.entity.CombinedCreditCardRequest;
import com.swivel.cc.base.domain.entity.CreditCardRequest;
import com.swivel.cc.base.domain.request.CreditCardRequestCreateRequestDto;
import com.swivel.cc.base.domain.request.CreditCardRequestUpdateRequestDto;
import com.swivel.cc.base.enums.CreditCardFilterType;
import com.swivel.cc.base.exception.InvalidCreditCardRequestException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.CreditCardRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class CreditCardService {

    private static final String ALL = "ALL";
    private static final String BANK = "BANK";
    private static final String SEARCH = "SEARCH";
    private static final String BIND = "_";
    private static final String INVALID_REQUEST_ID = "Invalid Credit card request Id: ";
    private static final String DB_ERROR = "Reading credit card request from the database was failed.";
    private final CreditCardRepository creditCardRepository;

    @Autowired
    public CreditCardService(CreditCardRepository creditCardRepository) {
        this.creditCardRepository = creditCardRepository;
    }

    /**
     * This method saves the credit card request.
     *
     * @param creditCardRequestCreateRequestDto creditCardRequestCreateRequestDto
     * @return CreditCardRequest
     */
    public CreditCardRequest saveCreditCardRequest(
            CreditCardRequestCreateRequestDto creditCardRequestCreateRequestDto) {
        try {
            var creditCardRequest = new CreditCardRequest(creditCardRequestCreateRequestDto);
            return creditCardRepository.save(creditCardRequest);
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving credit card request to database was failed", e);
        }
    }

    /**
     * This method updates the credit card request by id.
     *
     * @param creditCardRequestUpdateRequestDto creditCardRequestUpdateRequestDto
     * @return Credit Card Request
     */
    public CreditCardRequest updateCreditCardRequest(
            CreditCardRequestUpdateRequestDto creditCardRequestUpdateRequestDto) {
        try {
            var creditCardRequest = new CreditCardRequest(creditCardRequestUpdateRequestDto);
            return creditCardRepository.save(creditCardRequest);
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating credit card request to database was failed", e);
        }
    }

    /**
     * This method returns the credit card request by id.
     *
     * @param requestId request id.
     * @return CreditCardRequest
     */
    public CreditCardRequest getCreditCardRequestById(String requestId) {
        try {
            Optional<CreditCardRequest> optionalCreditCardRequest = creditCardRepository.findById(requestId);
            return optionalCreditCardRequest.orElseThrow(
                    () -> new InvalidCreditCardRequestException(INVALID_REQUEST_ID + requestId));
        } catch (DataAccessException e) {
            throw new QponCoreException(DB_ERROR, e);
        }
    }

    /**
     * This method searches credit card requests.
     *
     * @param pageable   pageable
     * @param bankId     bank id
     * @param searchTerm search term
     * @return CreditCardRequest page
     */
    public Page<CreditCardRequest> searchCreditCardRequests(Pageable pageable, String bankId, String searchTerm) {
        try {
            var creditCardFilterType = createFilterType(bankId, searchTerm);
            switch (creditCardFilterType) {
                case ALL_SEARCH:
                    return creditCardRepository.findAllBySearchTerm(pageable, searchTerm);
                case BANK_ALL:
                    return creditCardRepository.findAllByBankId(pageable, bankId);
                case BANK_SEARCH:
                    return creditCardRepository.findAllByBankIdWithSearch(pageable, bankId, searchTerm);
                default:
                    return creditCardRepository.findAll(pageable);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException(DB_ERROR, e);
        }
    }

    /**
     * This method deletes the credit card request by id.
     *
     * @param requestId request id
     */
    public void deleteCreditCardRequest(String requestId) {
        try {
            CreditCardRequest creditCardRequest = getCreditCardRequestById(requestId);
            creditCardRepository.delete(creditCardRequest);
        } catch (DataAccessException e) {
            throw new QponCoreException("Deleting credit card request from the database was failed.", e);
        }
    }

    /**
     * This method returns all the credit card request by user.
     *
     * @param userId user id.
     * @return List<CreditCardRequest>
     */
    public Page<CreditCardRequest> getCreditCardRequestByUserId(Pageable pageable, String userId) {
        try {
            return creditCardRepository.findAllByUserId(pageable, userId);
        } catch (DataAccessException e) {
            throw new QponCoreException(DB_ERROR, e);
        }
    }

    /**
     * This method combines bank id and  search term then returns the relevant enum.
     *
     * @param bankId     bank id
     * @param searchTerm search term
     * @return CreditCardFilterType String eg: ALL_ALL
     */
    private CreditCardFilterType createFilterType(String bankId, String searchTerm) {
        String part1 = bankId.equals(ALL) ? ALL : BANK;
        String part2 = searchTerm.equals(ALL) ? ALL : SEARCH;
        var value = part1 + BIND + part2;
        return CreditCardFilterType.valueOf(value);
    }

    /**
     * This method searches  grouped credit card request.
     *
     * @param pageable   pageable
     * @param bankId     bank Id
     * @param searchTerm search term
     * @return combinedCreditCardRequest Page
     */
    public Page<CombinedCreditCardRequest> getGroupCreditCardRequest(Pageable pageable, String bankId, String searchTerm) {
        try {
            if (bankId.equals(ALL) && !searchTerm.equals(ALL)) {
                return creditCardRepository.getAllGroupCreditCardRequestBySearchTerm(pageable, searchTerm);
            } else if (bankId.equals(ALL)) {
                return creditCardRepository.getAllGroupCreditCardRequests(pageable);
            } else if (searchTerm.equals(ALL)) {
                return creditCardRepository.getAllGroupCreditCardRequestByMerchantId(pageable, bankId);
            } else {
                return creditCardRepository.getAllGroupCreditCardRequestByMerchantIdAndSearchTerm(pageable, bankId, searchTerm);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("fetching combinedCreditCardRequests from the database was failed.", e);
        }
    }
}
