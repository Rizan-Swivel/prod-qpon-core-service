package com.swivel.cc.base.service;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.request.MailRequestDto;
import com.swivel.cc.base.domain.request.MobileNoRequestDto;
import com.swivel.cc.base.domain.request.SmsRequestDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.NotificationTemplateType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.wrapper.SendEmailResponseWrapper;
import com.swivel.cc.base.wrapper.SendSmsResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@Slf4j
public class NotificationService {

    private static final String USER = "<USER>";
    private static final String TIME_ZONE_HEADER = "Time-Zone";
    private static final String TIME_ZONE_VALUE = "Asia/Colombo";
    private static final String APP_KEY = "app-key";
    private final String utilServiceAppKey;
    private final String mailSendUri;
    private final String smsSendUri;
    private final RestTemplate restTemplate;
    private final Translator translator;

    @Autowired
    public NotificationService(@Value("${util.baseUrl}") String baseUrl,
                               @Value("${util.uri.sendMail}") String mailPath,
                               @Value("${util.uri.sendSms}") String smsPath,
                               @Value("${util.utilServiceAppKey}") String appKey,
                               RestTemplate restTemplate, Translator translator) {
        this.translator = translator;
        this.mailSendUri = baseUrl + mailPath;
        this.smsSendUri = baseUrl + smsPath;
        this.utilServiceAppKey = appKey;
        this.restTemplate = restTemplate;
    }

    /**
     * This method send mail
     *
     * @param receiver       receiver email
     * @param approvalStatus APPROVED/REJECTED
     * @param userName       receiver name
     */
    @Async
    public void sendMail(String receiver, ApprovalStatus approvalStatus, String userName) {

        var headers = getAuthHeaders();
        var mailRequestDto = approvalStatus == ApprovalStatus.APPROVED ?
                new MailRequestDto(receiver,
                        translator.toLocale(NotificationTemplateType.APPROVED.getEmailHeader()),
                        translator.toLocale(NotificationTemplateType.APPROVED.getEmailBody().
                                replace(USER, userName)))
                : new MailRequestDto(receiver,
                translator.toLocale(NotificationTemplateType.REJECTED.getEmailHeader()),
                translator.toLocale(NotificationTemplateType.REJECTED.getEmailBody()).replace(USER, userName));

        HttpEntity<MailRequestDto> entity = new HttpEntity<>(mailRequestDto, headers);
        try {
            log.debug("Calling util service to send the email to: {}", receiver);
            restTemplate.exchange(mailSendUri, HttpMethod.POST, entity, SendEmailResponseWrapper.class);
            log.debug("Sending email was success to: {}", receiver);
        } catch (HttpClientErrorException e) {
            log.error("Sending email was failed to: {}", receiver, e);
            throw new QponCoreException("Sending email was failed.", e);
        }
    }

    /**
     * This method send sms
     *
     * @param recipientNo    recipient mobile number
     * @param approvalStatus APPROVED/REJECTED
     */
    @Async
    public void sendSms(MobileNoRequestDto recipientNo, ApprovalStatus approvalStatus) {
        var headers = getAuthHeaders();
        var smsRequestDto = approvalStatus == ApprovalStatus.APPROVED ?
                new SmsRequestDto(recipientNo, translator.toLocale(NotificationTemplateType.APPROVED.getSms())) :
                new SmsRequestDto(recipientNo, translator.toLocale(NotificationTemplateType.REJECTED.getSms()));
        HttpEntity<SmsRequestDto> entity = new HttpEntity<>(smsRequestDto, headers);

        try {
            log.debug("Calling util service to send the sms to: {}", recipientNo.toLogJson());
            restTemplate.exchange(smsSendUri, HttpMethod.POST, entity, SendSmsResponseWrapper.class);
            log.debug("Sending sms was success to: {}", recipientNo.toLogJson());
        } catch (HttpClientErrorException e) {
            log.error("Sending sms was failed to: {}", recipientNo.toLogJson(), e);
            throw new QponCoreException("Sending sms was failed.", e);
        }
    }

    /**
     * This method returns headers for util service urls.
     */
    private HttpHeaders getAuthHeaders() {
        var headers = new HttpHeaders();
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(TIME_ZONE_HEADER, TIME_ZONE_VALUE);
        headers.set(APP_KEY, utilServiceAppKey);
        return headers;
    }
}
