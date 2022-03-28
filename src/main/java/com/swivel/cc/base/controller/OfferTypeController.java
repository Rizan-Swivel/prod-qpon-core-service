package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.entity.OfferType;
import com.swivel.cc.base.domain.request.OfferTypeCreateRequestDto;
import com.swivel.cc.base.domain.request.OfferTypeUpdateRequestDto;
import com.swivel.cc.base.domain.response.OfferTypeListResponseDto;
import com.swivel.cc.base.domain.response.OfferTypeRespondDto;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.exception.InvalidOfferTypeException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.service.OfferTypeService;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * OfferType controller
 */
@CrossOrigin
@Slf4j
@RestController
@Validated
@RequestMapping("api/v1/request-a-deal/offer-type")
public class OfferTypeController extends Controller {

    private final OfferTypeService offerTypeService;

    @Autowired
    public OfferTypeController(Translator translator, OfferTypeService offerTypeService) {
        super(translator);
        this.offerTypeService = offerTypeService;
    }


    /**
     * Create offer type
     *
     * @param timeZone                  time zone
     * @param userId                    user id
     * @param offerTypeCreateRequestDto offerTypeRequestDto
     * @return OfferTypeCreateRespondDto
     */
    @PostMapping
    public ResponseEntity<ResponseWrapper> createOfferType(@RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
                                                           @RequestHeader(name = USER_ID_HEADER) String userId,
                                                           @RequestBody OfferTypeCreateRequestDto offerTypeCreateRequestDto) {

        try {
            if (!offerTypeCreateRequestDto.isRequiredAvailable()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            var offerType = offerTypeService.saveOfferType(new OfferType(offerTypeCreateRequestDto.getName()));
            log.debug("Successfully created offer type. user id: {}, offer type response: {}", userId, offerType);
            return getSuccessResponse(new OfferTypeRespondDto(offerType.getId(), offerType.getName()),
                    SuccessResponseStatusType.CREATE_OFFER_TYPE);
        } catch (QponCoreException e) {
            log.error("Creating offer type was failed. userId: {}, time zone: {}, OfferRequest: {}", userId, timeZone,
                    offerTypeCreateRequestDto.toLogJson(), e);
            return getInternalServerError();
        }
    }

    /**
     * Get All offer types.
     *
     * @param timeZone time zone
     * @param userId   user id
     * @return offerTypeListResponseDto
     */
    @GetMapping
    public ResponseEntity<ResponseWrapper> getAllOfferTypes(@RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
                                                            @RequestHeader(name = USER_ID_HEADER) String userId) {
        try {
            var allOfferTypes = offerTypeService.getAllOfferTypes();
            log.debug("Successfully returned offer type list with the size of :{}. user id: {}",
                    allOfferTypes.size(), userId);
            var offerTypeListResponseDto = new OfferTypeListResponseDto(allOfferTypes);
            return getSuccessResponse(offerTypeListResponseDto, SuccessResponseStatusType.READ_OFFER_TYPE_LIST);

        } catch (QponCoreException e) {
            log.error("Returning List of offer types was failed. userId: {}, time zone: {}.", userId, timeZone, e);
            return getInternalServerError();
        }
    }

    @PutMapping
    public ResponseEntity<ResponseWrapper> updateOfferType(@RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
                                                           @RequestHeader(name = USER_ID_HEADER) String userId,
                                                           @RequestBody OfferTypeUpdateRequestDto offerTypeUpdateRequestDto) {

        try {
            if (!offerTypeUpdateRequestDto.isRequiredAvailable()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            var offerTypeById = offerTypeService.getOfferTypeById(offerTypeUpdateRequestDto.getId());
            var offerType = offerTypeService.updateOfferType(offerTypeById,
                    offerTypeUpdateRequestDto.getName());
            return getSuccessResponse(new OfferTypeRespondDto(offerType.getId(), offerType.getName()),
                    SuccessResponseStatusType.UPDATE_OFFER_TYPE);
        } catch (InvalidOfferTypeException e) {
            log.error("Invalid OfferType Id: {} for update Offer Type.", offerTypeUpdateRequestDto.getId(), e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_OFFER_TYPE_ID);
        } catch (QponCoreException e) {
            log.error("Updating offer type was failed. userId: {}, time zone: {}, OfferType Update Request: {}",
                    userId, timeZone, offerTypeUpdateRequestDto);
            return getInternalServerError();
        }
    }
}
