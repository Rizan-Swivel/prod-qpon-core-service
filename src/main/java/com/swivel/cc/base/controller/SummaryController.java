package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.response.FullSummaryResponseDto;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidUserException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.service.SummaryService;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;

/**
 * Summary Controller
 */
@RestController
@Slf4j
@RequestMapping("/api/v1/summary")
public class SummaryController extends Controller {

    private static final String INVALID_USER_ID = "Invalid userId: ";
    private final SummaryService summaryService;

    @Autowired
    public SummaryController(Translator translator, SummaryService summaryService) {
        super(translator);
        this.summaryService = summaryService;
    }

    /**
     * This method is used to get today's summary for admin & merchant.
     *
     * @param userId   userId
     * @param timeZone timeZone
     * @param userType userType
     * @param request  http request
     * @return today's summary response.
     */
    @GetMapping("/{userType}")
    public ResponseEntity<ResponseWrapper> getTodaySummary(@RequestHeader(name = USER_ID_HEADER) String userId,
                                                           @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
                                                           @PathVariable UserType userType,
                                                           HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            FullSummaryResponseDto fullSummaryResponseDto =
                    summaryService.getFullSummary(timeZone, userType, userId, authToken);
            return getSuccessResponse(fullSummaryResponseDto, SuccessResponseStatusType.SUCCESSFULLY_RETURNED_SUMMARY);
        } catch (InvalidUserException e) {
            log.error("Approved business profile not found for userId: {}, userType: {}", userId, userType, e);
            return getErrorResponseForInvalidUserException(e.getMessage());
        } catch (QponCoreException e) {
            log.error("Returning today's summary failed for userId: {}, userType: {}", userId, userType, e);
            return getInternalServerError();
        }
    }

    /**
     * This method is used to display error message for InvalidUserExceptions.
     *
     * @param errorMessage exception message.
     * @return error response.
     */
    private ResponseEntity<ResponseWrapper> getErrorResponseForInvalidUserException(String errorMessage) {
        if (errorMessage.startsWith(INVALID_USER_ID))
            return getErrorResponse(ErrorResponseStatusType.INVALID_USER_ID_OR_TYPE);
        else
            return getErrorResponse(ErrorResponseStatusType.BUSINESS_PROFILE_NOT_FOUND);
    }
}
