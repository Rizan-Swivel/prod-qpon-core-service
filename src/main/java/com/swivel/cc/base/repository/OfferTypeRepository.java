package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.OfferType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

/**
 * Offer Type repository
 */
public interface OfferTypeRepository extends JpaRepository<OfferType, String> {

    /**
     * This method used to get all Offer type list order by updatedAt field.
     *
     * @return Offer Type list
     */
    List<OfferType> findAllByOrderByUpdatedAtDesc();
}
