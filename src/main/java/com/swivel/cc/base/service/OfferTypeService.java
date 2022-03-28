package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.OfferType;
import com.swivel.cc.base.exception.InvalidOfferTypeException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.OfferTypeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OfferTypeService {

    private static final String INVALID_OFFER_TYPE_ID = "Invalid Offer Type Id: ";

    private final OfferTypeRepository offerTypeRepository;

    @Autowired
    public OfferTypeService(OfferTypeRepository offerTypeRepository) {
        this.offerTypeRepository = offerTypeRepository;
    }


    /**
     * Save offer type in to database
     *
     * @param offerType offer type
     * @return offer type
     */
    public OfferType saveOfferType(OfferType offerType) {
        try {
            return offerTypeRepository.save(offerType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving offer type into database was failed.", e);
        }
    }

    /**
     * Update Offer Type
     *
     * @param offerType offerType
     * @param name      name
     * @return offer type
     */
    public OfferType updateOfferType(OfferType offerType, String name) {
        try {
            offerType.setName(name);
            offerType.setUpdatedAt(System.currentTimeMillis());
            return offerTypeRepository.save(offerType);
        } catch (DataAccessException e) {
            throw new QponCoreException("updating offer type into database was failed.", e);
        }
    }


    /**
     * Get all offer types
     *
     * @return offer type list
     */
    public List<OfferType> getAllOfferTypes() {
        try {
            return offerTypeRepository.findAllByOrderByUpdatedAtDesc();
        } catch (DataAccessException e) {
            throw new QponCoreException("Getting offer type list from database was failed.", e);
        }
    }

    /**
     * get offer type by id
     *
     * @param id id
     * @return offer type
     */
    public OfferType getOfferTypeById(String id) {
        try {
            Optional<OfferType> optionalOfferType = offerTypeRepository.findById(id);

            if (optionalOfferType.isPresent()) {
                return optionalOfferType.get();
            } else {
                throw new InvalidOfferTypeException(INVALID_OFFER_TYPE_ID + id);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading Offer Type from database was failed.", e);
        }
    }
}
