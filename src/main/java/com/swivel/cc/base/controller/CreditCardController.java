package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.Container.ValidityContainerDto;
import com.swivel.cc.base.domain.entity.CombinedCreditCardRequest;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.request.CreditCardRequestCreateRequestDto;
import com.swivel.cc.base.domain.request.CreditCardRequestUpdateRequestDto;
import com.swivel.cc.base.domain.response.CreditCardListResponseDto;
import com.swivel.cc.base.domain.response.CreditCardRequestResponseDto;
import com.swivel.cc.base.domain.response.DetailCreditCardRequestResponseDto;
import com.swivel.cc.base.domain.response.ListCombinedCreditCardRequestResponseDto;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidBankException;
import com.swivel.cc.base.exception.InvalidCreditCardRequestException;
import com.swivel.cc.base.exception.InvalidUserException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.service.AuthUserService;
import com.swivel.cc.base.service.CreditCardService;
import com.swivel.cc.base.util.Validator;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;
import java.util.ArrayList;
import java.util.List;

/**
 * Credit card Controller
 */
@CrossOrigin
@Slf4j
@RestController
@Validated
@RequestMapping("api/v1/banks/credit-cards")
public class CreditCardController extends Controller {

    protected static final String DEFAULT_SORT = "createdAt";
    private final AuthUserService authUserService;
    private final CreditCardService creditCardService;
    private final Validator validator;

    public CreditCardController(Translator translator, AuthUserService authUserService,
                                CreditCardService creditCardService, Validator validator) {
        super(translator);
        this.authUserService = authUserService;
        this.creditCardService = creditCardService;
        this.validator = validator;
    }

    /**
     * Create Credit Card Request.
     *
     * @param timeZone                          time zone
     * @param userId                            user id
     * @param creditCardRequestCreateRequestDto creditCardRequestCreateRequestDto
     * @return creditCardRequestResponseDto
     */
    @PostMapping("")
    public ResponseEntity<ResponseWrapper> createCreditCardRequest(
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestBody CreditCardRequestCreateRequestDto creditCardRequestCreateRequestDto,
            HttpServletRequest request) {
        try {
            var validityContainerDto = checkValidity(validator, creditCardRequestCreateRequestDto);
            if (!validityContainerDto.isValid()) {
                return getErrorResponse(validityContainerDto.getErrorResponseStatusType());
            }
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            authUserService.getUserByUserId(creditCardRequestCreateRequestDto.getBankId(), authToken);
            authUserService.getUserByUserId(creditCardRequestCreateRequestDto.getUserId(), authToken);
            var creditCardRequest = creditCardService.saveCreditCardRequest(creditCardRequestCreateRequestDto);
            var creditCardRequestResponseDto = new CreditCardRequestResponseDto(creditCardRequest, timeZone);
            log.debug("Successfully created credit card request for userId: {} , bankId: {}",
                    userId, creditCardRequestResponseDto.getBankId());
            return getSuccessResponse(creditCardRequestResponseDto,
                    SuccessResponseStatusType.CREATE_CREDIT_CARD_REQUEST);
        } catch (InvalidUserException e) {
            log.error("Invalid/ Inactive user: {} or Invalid/ Inactive bank :{}",
                    userId, creditCardRequestCreateRequestDto.getBankId(), e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_USER_OR_BANK);
        } catch (QponCoreException e) {
            log.error("Creating a credit card request was failed for userId: {}, bankId: {},timeZone: {},",
                    userId, creditCardRequestCreateRequestDto.getBankId(), timeZone);
            return getInternalServerError();
        }
    }

    /**
     * Update Credit card Request.
     *
     * @param timeZone                          time zone
     * @param userId                            user id
     * @param creditCardRequestUpdateRequestDto creditCardRequestUpdateRequestDto
     * @return creditCardRequestResponseDto
     */
    @PutMapping("")
    public ResponseEntity<ResponseWrapper> updateCreditCardRequest(
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestBody CreditCardRequestUpdateRequestDto creditCardRequestUpdateRequestDto,
            HttpServletRequest request) {
        try {
            var validityContainerDto = checkValidity(validator, creditCardRequestUpdateRequestDto);
            if (!validityContainerDto.isValid()) {
                return getErrorResponse(validityContainerDto.getErrorResponseStatusType());
            }
            creditCardService.getCreditCardRequestById(creditCardRequestUpdateRequestDto.getId());
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            authUserService.getUserByUserId(creditCardRequestUpdateRequestDto.getBankId(), authToken);
            authUserService.getUserByUserId(creditCardRequestUpdateRequestDto.getUserId(), authToken);
            var creditCardRequest = creditCardService
                    .updateCreditCardRequest(creditCardRequestUpdateRequestDto);
            var creditCardRequestResponseDto = new CreditCardRequestResponseDto(creditCardRequest, timeZone);
            log.debug("Successfully updated credit card request for id: {}.", creditCardRequestUpdateRequestDto.getId());
            return getSuccessResponse(creditCardRequestResponseDto,
                    SuccessResponseStatusType.UPDATE_CREDIT_CARD_REQUEST);
        } catch (InvalidCreditCardRequestException e) {
            log.error("update credit card request for id: {} was failed for user Id: {}, bank id: {}.",
                    creditCardRequestUpdateRequestDto.getId(), creditCardRequestUpdateRequestDto.getUserId(),
                    creditCardRequestUpdateRequestDto.getBankId(), e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CREDIT_CARD_REQUEST_ID);
        } catch (InvalidUserException e) {
            log.error("Invalid/ Inactive user: {} or Invalid/ Inactive bank :{} for update credit card request.",
                    creditCardRequestUpdateRequestDto.getUserId(), creditCardRequestUpdateRequestDto.getBankId(), e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_USER_OR_BANK);
        } catch (QponCoreException e) {
            log.error("Credit card request update was failed for creditCardRequestId: {}, userId: {}," +
                            " bankId: {},timeZone: {},", creditCardRequestUpdateRequestDto.getId(),
                    userId, creditCardRequestUpdateRequestDto.getBankId(), timeZone);
            return getInternalServerError();
        }
    }

    /**
     * Detail Credit Card Request By id.
     *
     * @param userId    user id
     * @param timeZone  time zone
     * @param requestId request id
     * @return detailCreditCardRequestResponseDto
     */
    @GetMapping("/requests/{request-id}")
    public ResponseEntity<ResponseWrapper> getCreditCardRequestDetail(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable("request-id") String requestId) {
        try {
            var creditCardRequest = creditCardService.getCreditCardRequestById(requestId);
            var detailCreditCardRequestResponseDto = new DetailCreditCardRequestResponseDto(creditCardRequest, timeZone);
            return getSuccessResponse(detailCreditCardRequestResponseDto,
                    SuccessResponseStatusType.READ_CREDIT_CARD_REQUEST);
        } catch (InvalidCreditCardRequestException e) {
            log.error("Invalid credit card request id: {} for user Id: {}", requestId, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CREDIT_CARD_REQUEST_ID);
        } catch (QponCoreException e) {
            log.error("Error when reading credit card request for requestId {}, userId: {}", requestId, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Bank - Search List of CC requests.
     *
     * @param userId     user id
     * @param timeZone   time zone
     * @param bankId     bank id/ ALL
     * @param page       page
     * @param size       size
     * @param searchTerm search term/ ALL
     * @return CreditCardListResponseDto
     */
    @GetMapping("/{bankId}/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> searchCreditCardRequestList(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String bankId,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String searchTerm) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            var creditCardRequests =
                    creditCardService.searchCreditCardRequests(pageable, bankId, searchTerm);
            var creditCardListResponseDto = new CreditCardListResponseDto(creditCardRequests, timeZone);
            return getSuccessResponse(
                    creditCardListResponseDto, SuccessResponseStatusType.READ_CREDIT_CARD_REQUEST_LIST);
        } catch (InvalidBankException e) {
            log.error("Invalid bank id: {} to get credit card request list.", bankId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BANK_ID);
        } catch (QponCoreException e) {
            log.error("Returning credit card request was failed for page: {}, size {}, bankId: {} and userId: {}",
                    page, size, bankId, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Permanently delete CC Request.
     *
     * @param userId    user id
     * @param timeZone  time zone
     * @param requestId request id
     * @return SuccessResponse / ErrorResponse
     */
    @DeleteMapping("/requests/{request-id}")
    public ResponseEntity<ResponseWrapper> deleteCreditCardRequest(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable("request-id") String requestId) {
        try {
            creditCardService.deleteCreditCardRequest(requestId);
            return getSuccessResponse(null, SuccessResponseStatusType.DELETE_CREDIT_CARD_REQUEST);
        } catch (InvalidCreditCardRequestException e) {
            log.error("Invalid credit card request id: {}.", requestId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_CREDIT_CARD_REQUEST_ID);
        } catch (QponCoreException e) {
            log.error("Deleting credit card request was failed for request id: {}, user id:{}.", requestId, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Credit Card Requests list for the mobile user.
     *
     * @param userId   user id
     * @param timeZone time zone
     * @param page     page
     * @param size     size
     * @return CreditCardListResponseDto
     */
    @GetMapping("/user/{user-id}/{page}/{size}")
    public ResponseEntity<ResponseWrapper> listCreditCardRequestForAUser(
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable("user-id") String userId,
            HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            authUserService.getUserByUserId(userId, authToken);
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            var creditCardRequestByUser =
                    creditCardService.getCreditCardRequestByUserId(pageable, userId);
            var creditCardListResponseDto = new CreditCardListResponseDto(creditCardRequestByUser, timeZone);
            return getSuccessResponse(
                    creditCardListResponseDto, SuccessResponseStatusType.READ_CREDIT_CARD_REQUEST_LIST);
        } catch (InvalidUserException e) {
            log.error("Invalid user id :{}", userId, e);
            return getErrorResponse(ErrorResponseStatusType.INACTIVE_MERCHANT_FOR_CREATE_DEAL);
        } catch (QponCoreException e) {
            log.error("Returning credit card request by user id was failed for user id : {}, page: {}, size {}.",
                    userId, page, size, e);
            return getInternalServerError();
        }
    }

    /**
     * Checks validation for the fields from creditCardRequest and set data for attributes.
     *
     * @param validator                         validator
     * @param creditCardRequestCreateRequestDto creditCardRequestCreateRequestDto
     * @return ValidityContainerDto
     */
    private ValidityContainerDto checkValidity(Validator validator,
                                               CreditCardRequestCreateRequestDto creditCardRequestCreateRequestDto) {
        if (!validator
                .isValidMobileNoWithCountryCode(creditCardRequestCreateRequestDto.getMobileNumber().getNo())) {
            return new ValidityContainerDto(false, ErrorResponseStatusType.INVALID_MOBILE_NUMBER);
        }
        if (!validator
                .isValidEmail(creditCardRequestCreateRequestDto.getEmail())) {
            return new ValidityContainerDto(false, ErrorResponseStatusType.INVALID_EMAIL);
        }
        if (!validator.isValidNIC(creditCardRequestCreateRequestDto.getNic())) {
            return new ValidityContainerDto(false, ErrorResponseStatusType.INVALID_NIC);
        }
        return new ValidityContainerDto(true, null);
    }


    /**
     * Grouped Credit Card request list for Admin and Bank.
     *
     * @param userId     userId
     * @param timeZone   time zone
     * @param page       page
     * @param size       size
     * @param bankId     bankId
     * @param searchTerm searchTerm
     * @return ListCombinedCreditCardRequestResponseDto
     */
    @GetMapping("/combinations/{page}/{size}/bank/{bankId}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getGroupCreditCardRequests(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable("page") int page,
            @PathVariable("size") int size,
            @PathVariable("bankId") String bankId,
            @PathVariable("searchTerm") String searchTerm,
            HttpServletRequest request) {
        try {
            String authToken = request.getHeader(AUTH_TOKEN_HEADER);
            if (!bankId.equals(ALL)) {
                authUserService.getBankBusinessByBankId(authToken, bankId);
            }
            Pageable pageable = PageRequest.of(page, size);
            var groupCreditCardRequest =
                    creditCardService.getGroupCreditCardRequest(pageable, bankId, searchTerm);
            if (!groupCreditCardRequest.isEmpty()) {
                var listCombinedCreditCardRequestResponseDto =
                        createListCombinedCreditCardRequestResponseDto(groupCreditCardRequest, userId);
                log.info("successfully fetched combinedCreditCardRequestList for user id: {}, bank id: {}," +
                        " search term: {}.", userId, bankId, searchTerm);
                return getSuccessResponse(listCombinedCreditCardRequestResponseDto,
                        SuccessResponseStatusType.READ_REQUEST_A_CREDIT_CARD_GROUP);
            }
            var listCombinedCreditCardRequestResponseDto =
                    new ListCombinedCreditCardRequestResponseDto(groupCreditCardRequest, null);
            return getSuccessResponse(listCombinedCreditCardRequestResponseDto,
                    SuccessResponseStatusType.READ_REQUEST_A_CREDIT_CARD_GROUP);
        } catch (InvalidBankException e) {
            log.error("Invalid bank id: {} for get grouped credit card request list.", bankId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BANK_ID);
        } catch (QponCoreException e) {
            log.error("Reading grouped credit card request list is failed for page: {}, size: {}, " +
                    "bankId: {}, searchTerm: {}, userId: {}", page, size, bankId, searchTerm, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * create listCombinedCreditCardRequest from a creditCardRequestsPage.
     *
     * @param creditCardRequests creditCardRequestsPage
     * @param userId             user id
     * @return List combinedCreditCardRequestResponseDto
     */
    private ListCombinedCreditCardRequestResponseDto createListCombinedCreditCardRequestResponseDto(
            Page<CombinedCreditCardRequest> creditCardRequests, String userId) {
        List<String> userIds = new ArrayList<>();
        creditCardRequests.getContent().forEach(combinedCreditCardRequest ->
                userIds.add(combinedCreditCardRequest.getBankId()));
        var bulkUserRequestDto = new BulkUserRequestDto(userIds);
        var bulkUserResponseDto =
                authUserService.getMerchantMap(userId, bulkUserRequestDto, UserType.BANK);
        return new ListCombinedCreditCardRequestResponseDto(creditCardRequests, bulkUserResponseDto);
    }
}