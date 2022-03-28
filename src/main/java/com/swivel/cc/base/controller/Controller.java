package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.response.ResponseDto;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.ResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.wrapper.ErrorResponseWrapper;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import com.swivel.cc.base.wrapper.SuccessResponseWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.TimeZone;

/**
 * Controller
 */
public class Controller {

    protected static final String DEFAULT_SORT = "updatedAt";
    protected static final String DEFAULT_NATIVE_SORT = "updated_at";
    protected static final String EXPIRED_ON_SORT = "expiredOn";
    protected static final String ADMIN_ROLE = "ADMIN";
    protected static final String MERCHANT_ROLE = "MERCHANT";
    protected static final String USER_ID_HEADER = "User-Id";
    protected static final String TIME_ZONE_HEADER = "Time-Zone";
    protected static final int DEFAULT_PAGE = 0;
    protected static final int PAGE_MAX_SIZE = 250;
    protected static final String UNSUPPORTED_USER_TYPE_ACTION_LOG =
            "Unsupported userType: {} to perform the action: {}";
    protected static final String AUTH_TOKEN_HEADER = "Auth-Token";
    protected static final String ALL = "ALL";
    protected final Translator translator;

    @Autowired
    public Controller(Translator translator) {
        this.translator = translator;
    }

    /**
     * This method creates the data response for success request.
     *
     * @param responseDto responseDto
     * @return response entity
     */
    protected ResponseEntity<ResponseWrapper> getSuccessResponse(ResponseDto responseDto,
                                                                 SuccessResponseStatusType successResponseStatusType) {

        var successResponseWrapper = new SuccessResponseWrapper(ResponseStatusType.SUCCESS,
                successResponseStatusType, responseDto,
                translator.toLocale(successResponseStatusType.getCodeString(successResponseStatusType.getCode())));
        return new ResponseEntity<>(successResponseWrapper, HttpStatus.OK);
    }

    /**
     * This method creates the internal server error response.
     *
     * @return response entity
     */
    protected ResponseEntity<ResponseWrapper> getInternalServerError() {
        var errorResponseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR,
                ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getMessage(), null,
                translator.toLocale(ErrorResponseStatusType.
                        getCodeString(ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode())),
                ErrorResponseStatusType.INTERNAL_SERVER_ERROR.getCode());
        return new ResponseEntity<>(errorResponseWrapper, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    /**
     * This method creates the empty data response for bad request.
     *
     * @param errorsResponseStatusType errorResponseStatusType
     * @return bad request error response
     */
    protected ResponseEntity<ResponseWrapper> getErrorResponse(ErrorResponseStatusType errorsResponseStatusType) {
        var errorResponseWrapper = new ErrorResponseWrapper(ResponseStatusType.ERROR,
                errorsResponseStatusType.getMessage(), null,
                translator.toLocale(ErrorResponseStatusType.getCodeString(errorsResponseStatusType.getCode())),
                errorsResponseStatusType.getCode());
        return new ResponseEntity<>(errorResponseWrapper, HttpStatus.BAD_REQUEST);
    }
}
