package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.AuthUser;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.response.*;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidUserException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.wrapper.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Slf4j
@Service
public class AuthUserService {
    protected static final String TIME_ZONE_HEADER = "Time-Zone";
    protected static final String APP_KEY = "app-key";
    protected static final String TIME_ZONE_VALUE = "Asia/Colombo";
    private static final String USER_ID_HEADER = "User-Id";
    private static final String FAILED_GET_USER_BY_ID = "Getting user by id was failed from auth service";
    private static final String FAILED_GET_USER_DATA = "Requesting user data from auth service was failed.";
    private static final String FAILED_LOG_MESSAGE = "{}. statusCode: {}, body: {}";
    private static final String INVALID_USER_ID = "Invalid userId: ";
    private static final String INVALID_USER_TYPE = "Invalid userType: ";
    private static final String FAILED_TO_GET_BUSINESS_INFO = "Failed to get approved business info from auth service. userId: ";
    private static final String FAILED_TO_GET_TODAY_SUMMARY = "Failed to get today's summary from auth service. ";
    private static final String MERCHANT_ID_REPLACE_PHASE = "##MERCHANT-ID##";
    private static final String BULK_INFO_REPLACE_PHASE = "##TO-USER-TYPE##";
    private static final String BANK_ID_REPLACE_PHASE = "##BANK-ID##";
    private static final String USER_TYPE_REPLACE_PHASE = "##TO-USER-TYPE##";
    private static final String OR = " or ";
    private final String getUserUrl;
    private final RestTemplate restTemplate;
    private final String getBulkMerchantsInfo;
    private final String getBulkUsersInfo;
    private final String getMerchantInfoUrl;
    private final String getBankInfoUrl;
    private final String getTodaySummaryUrl;
    private final String getAppKey;

    @Autowired
    public AuthUserService(@Value("${auth.baseUrl}") String baseUrl,
                           @Value("${auth.uri.getUser}") String getUserUri,
                           @Value("${auth.uri.getBulkMerchantInfo}") String getBulkMerchantInfo,
                           @Value("${auth.uri.getBulkUserInfo}") String getBulkUserInfo,
                           @Value("${auth.uri.getMerchantInfo}") String getMerchantInfo,
                           @Value("${auth.uri.getBankInfo}") String getBankInfo,
                           @Value("${auth.uri.getTodaySummary}") String getTodaySummaryUrl,
                           @Value("${auth.appKey}") String getAppKey,
                           RestTemplate restTemplate) {
        this.getUserUrl = baseUrl + getUserUri;
        this.getBulkMerchantsInfo = baseUrl + getBulkMerchantInfo;
        this.getBulkUsersInfo = baseUrl + getBulkUserInfo;
        this.getMerchantInfoUrl = baseUrl + getMerchantInfo;
        this.getBankInfoUrl = baseUrl + getBankInfo;
        this.getTodaySummaryUrl = baseUrl + getTodaySummaryUrl;
        this.restTemplate = restTemplate;
        this.getAppKey = getAppKey;
    }

    /**
     * This method get merchant business info by merchant id.
     *
     * @param token      token
     * @param merchantId merchant id
     * @return Merchant Business info
     */
    public BusinessMerchantResponseDto getMerchantBusinessByMerchantId(String token, String merchantId) {
        HttpHeaders headers = getAuthHeaders(token, merchantId);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            var url = getMerchantInfoUrl.replace(MERCHANT_ID_REPLACE_PHASE, merchantId);
            log.debug("Calling auth service to get the merchant business info by merchant id. url: {}, merchantId: {}",
                    url, merchantId);
            var result =
                    restTemplate.exchange(url, HttpMethod.GET, entity, MerchantBusinessResponseWrapper.class);
            String responseBody = Objects.requireNonNull(result.getBody()).getData().toLogJson();
            log.debug("Getting merchant business info by id was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            return Optional.ofNullable(result.getBody().getData()).get();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new InvalidUserException(FAILED_TO_GET_BUSINESS_INFO + merchantId, e);
            }
            throw new QponCoreException(FAILED_TO_GET_BUSINESS_INFO + merchantId, e);
        }
    }

    /**
     * This method get bank business info by bank id.
     *
     * @param token  token
     * @param bankId bankId
     * @return Bank business info.
     */
    public BusinessMerchantResponseDto getBankBusinessByBankId(String token, String bankId) {
        HttpHeaders headers = getAuthHeaders(token, bankId);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            var url = getBankInfoUrl.replace(BANK_ID_REPLACE_PHASE, bankId);
            log.debug("Calling auth service to get the bank business info by bank id. url: {}, bankId: {}",
                    url, bankId);
            var result =
                    restTemplate.exchange(url, HttpMethod.GET, entity, MerchantBusinessResponseWrapper.class);
            String responseBody = Objects.requireNonNull(result.getBody()).getData().toLogJson();
            log.debug("Getting bank business info by id was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            return Optional.ofNullable(result.getBody().getData()).get();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().equals(HttpStatus.BAD_REQUEST)) {
                throw new InvalidUserException(FAILED_TO_GET_BUSINESS_INFO + bankId, e);
            }
            throw new QponCoreException(FAILED_TO_GET_BUSINESS_INFO + bankId, e);
        }
    }

    /**
     * This method get auth user by id.
     *
     * @param userId userId
     * @param token  token
     * @return auth user
     */
    public AuthUser getUserByUserId(String userId, String token) {
        HttpHeaders headers = getAuthHeaders(token, userId);
        HttpEntity<String> entity = new HttpEntity<>(null, headers);
        try {
            log.debug("Calling auth service to get the user by Id. url: {}, userId: {}", getUserUrl, userId);
            ResponseEntity<AuthResponseWrapper> result =
                    restTemplate.exchange(getUserUrl, HttpMethod.GET, entity, AuthResponseWrapper.class);
            String responseBody = Objects.requireNonNull(result.getBody()).getData().toLogJson();
            log.debug("Getting user by id was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            Optional<AuthUser> authUserOptional = Optional.ofNullable(result.getBody().getData());
            return authUserOptional.get();
        } catch (HttpClientErrorException e) {
            if (e.getStatusCode().value() == HttpStatus.BAD_REQUEST.value()) {
                throw new InvalidUserException(INVALID_USER_ID + userId);
            }
            throw new QponCoreException(FAILED_GET_USER_DATA, e);
        }
    }

    /**
     * This method returns merchant business details for blog
     *
     * @param userId  userId
     * @param userIds userIdsgetBulkMerchantBusinessResponse
     * @return BulkMerchantBusinessResponseDto
     */
    private BulkMerchantBusinessResponseDto getBulkMerchantBusinessResponse(String userId, BulkUserRequestDto userIds, UserType toUserType) {
        HttpHeaders headers = getAuthHeadersForBulkMerchant();
        HttpEntity<BulkUserRequestDto> entity = new HttpEntity<>(userIds, headers);
        String getBulkInfoUrl = (UserType.MERCHANT.equals(toUserType)) ? getBulkMerchantsInfo
                .replace(BULK_INFO_REPLACE_PHASE, UserType.MERCHANT.toString()) :
                getBulkMerchantsInfo.replace(BULK_INFO_REPLACE_PHASE, UserType.BANK.toString());
        try {
            log.debug("Calling auth service to get merchant business bulk by Id. url: {}, userId: {}",
                    getBulkInfoUrl, userId);
            ResponseEntity<BulkMerchantResponseWrapper> result =
                    restTemplate.exchange(getBulkInfoUrl, HttpMethod.POST, entity, BulkMerchantResponseWrapper.class);
            String responseBody = result.getBody() == null ? "" : result.getBody().toLogJson();
            log.debug("Getting merchant business bulk by id was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            return result.getBody().getData();
        } catch (HttpClientErrorException e) {
            log.error(FAILED_LOG_MESSAGE, FAILED_GET_USER_BY_ID, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new QponCoreException(FAILED_GET_USER_BY_ID, e);
        }
    }

    /**
     * This method is used to get today's summary from auth service.
     *
     * @param userId userId
     * @param token  token
     * @return today summary response.
     */
    public TodayAuthSummaryResponseDto getTodaySummaryResponse(String userId, String token, String userType) {
        HttpHeaders headers = getAuthHeaders(token, userId);
        HttpEntity<BulkUserRequestDto> entity = new HttpEntity<>(null, headers);
        try {
            var url = getTodaySummaryUrl.replace(USER_TYPE_REPLACE_PHASE, userType);
            log.debug("Calling auth service to get today summary. Url: {}, UserId: {}", getTodaySummaryUrl, userId);
            ResponseEntity<TodayAuthSummaryResponseWrapper> result =
                    restTemplate.exchange(url, HttpMethod.GET, entity, TodayAuthSummaryResponseWrapper.class);
            String responseBody = result.getBody() == null ? "" : result.getBody().toLogJson();
            log.debug("Getting today summary was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            return result.getBody().getData();
        } catch (HttpClientErrorException e) {
            log.error(FAILED_LOG_MESSAGE, FAILED_TO_GET_TODAY_SUMMARY, e.getRawStatusCode(), e.getResponseBodyAsString());
            if (e.getStatusCode().value() == HttpStatus.BAD_REQUEST.value()) {
                throw new InvalidUserException(INVALID_USER_ID + userId + OR + INVALID_USER_TYPE + userType);
            }
            throw new QponCoreException(FAILED_TO_GET_TODAY_SUMMARY, e);
        }
    }

    /**
     * This method returns a Merchant/ Bank Map by user id and bulk request dto
     *
     * @param userId     user id
     * @param userIds    BulkUser Id List
     * @param toUserType toUserType
     * @return Merchant map
     */
    public Map<String, MerchantBusinessResponseDto> getMerchantMap(String userId,
                                                                   BulkUserRequestDto userIds, UserType toUserType) {
        var bulkUserResponse = getBulkMerchantBusinessResponse(userId, userIds, toUserType);
        Map<String, MerchantBusinessResponseDto> merchantResponseDtoHashMap = new HashMap<>();
        for (MerchantBusinessResponseDto merchantBusinessResponseDto : bulkUserResponse.getMerchants()) {
            merchantResponseDtoHashMap.put(merchantBusinessResponseDto.getId(), merchantBusinessResponseDto);
        }
        return merchantResponseDtoHashMap;
    }

    /**
     * This method returns a Merchant Map for deal page.
     *
     * @param dealPage deal page
     * @param userId   user id
     * @return Merchant map
     */
    public Map<String, MerchantBusinessResponseDto> getMerchantMapForDeals(Page<Deal> dealPage, String userId,
                                                                           UserType userType) {
        List<String> userIds = new ArrayList<>();
        if (!dealPage.getContent().isEmpty()) {
            dealPage.getContent().forEach(deal -> userIds.add(deal.getMerchantId()));
            BulkUserRequestDto bulkUserRequestDto = new BulkUserRequestDto(userIds);
            return getMerchantMap(userId, bulkUserRequestDto, userType);
        }
        return new HashMap<>();
    }

    /**
     * This method returns headers for auth service urls.
     *
     * @param token token
     * @return headers
     */
    private HttpHeaders getAuthHeaders(String token, String userId) {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(token.trim());
        headers.set(TIME_ZONE_HEADER, TIME_ZONE_VALUE);
        headers.set(USER_ID_HEADER, userId);
        return headers;
    }

    /**
     * This method returns header for getBulkMerchant url.
     *
     * @return headers
     */
    private HttpHeaders getAuthHeadersForBulkMerchant() {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(TIME_ZONE_HEADER, TIME_ZONE_VALUE);
        headers.set(APP_KEY, getAppKey);
        return headers;
    }

    /**
     * This method returns a Users Map by user id and bulk request dto
     *
     * @param userId  user id
     * @param token   token
     * @param userIds BulkUser Id List
     * @return Merchant map
     */
    public Map<String, BasicUserResponseDto> getUserMap(String userId, String token,
                                                        BulkUserRequestDto userIds) {
        var bulkUserResponse = getBulkUserResponse(userId, token, userIds);
        Map<String, BasicUserResponseDto> userResponseDtoHashMap = new HashMap<>();

        for (BasicUserResponseDto basicUserResponseDto : bulkUserResponse.getUsers()) {
            userResponseDtoHashMap.put(basicUserResponseDto.getId(), basicUserResponseDto);
        }
        return userResponseDtoHashMap;
    }

    /**
     * This method returns user details
     *
     * @param userId  userId
     * @param token   token
     * @param userIds userIds
     * @return BulkMerchantBusinessResponseDto
     */
    private BulkUserResponseDto getBulkUserResponse(String userId, String token, BulkUserRequestDto userIds) {
        HttpHeaders headers = getAuthHeaders(token, userId);
        HttpEntity<BulkUserRequestDto> entity = new HttpEntity<>(userIds, headers);
        try {
            log.debug("Calling auth service to get user bulk by Id. url: {}, userId: {}",
                    getBulkUsersInfo, userId);
            ResponseEntity<BulkUserResponseWrapper> result =
                    restTemplate.exchange(getBulkUsersInfo, HttpMethod.POST, entity, BulkUserResponseWrapper.class);
            String responseBody = result.getBody() == null ? "" : result.getBody().toLogJson();

            log.debug("Getting user bulk by id was successful. statusCode: {}, response: {}",
                    result.getStatusCode(), responseBody);
            return result.getBody().getData();
        } catch (HttpClientErrorException e) {
            log.error(FAILED_LOG_MESSAGE, FAILED_GET_USER_BY_ID, e.getRawStatusCode(), e.getResponseBodyAsString());
            throw new QponCoreException(FAILED_GET_USER_BY_ID, e);
        }
    }
}