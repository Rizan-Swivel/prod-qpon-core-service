package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.request.DealApprovalStatusUpdateRequestDto;
import com.swivel.cc.base.domain.request.MobileNoRequestDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Deal approval service
 */
@Service
public class DealApprovalService {

    private final AuthUserService authUserService;
    private final NotificationService notificationService;

    @Autowired
    public DealApprovalService(AuthUserService authUserService, NotificationService notificationService) {
        this.authUserService = authUserService;
        this.notificationService = notificationService;
    }

    /**
     * This method send mail & SMS to merchant after reject a deal
     *
     * @param deal                               deal
     * @param dealApprovalStatusUpdateRequestDto dealApprovalStatusUpdateRequestDto
     * @param authToken                          authToken
     */
    public void sendMailAndSmsToMerchant(Deal deal,
                                         DealApprovalStatusUpdateRequestDto dealApprovalStatusUpdateRequestDto,
                                         String authToken) {
        var authUser = authUserService.getUserByUserId(deal.getMerchantId(), authToken);
        if (authUser.getEmail() != null && authUser.getMobileNo() != null) {
            notificationService.sendMail(authUser.getEmail(),
                    dealApprovalStatusUpdateRequestDto.getApprovalStatus(), authUser.getFullName());
            notificationService.sendSms(new MobileNoRequestDto(authUser.getMobileNo().getCountryCode(),
                            authUser.getMobileNo().getLocalNumber()),
                    dealApprovalStatusUpdateRequestDto.getApprovalStatus());
        } else if (authUser.getEmail() != null) {
            notificationService.sendMail(authUser.getEmail(),
                    dealApprovalStatusUpdateRequestDto.getApprovalStatus(), authUser.getFullName());
        } else if (authUser.getMobileNo() != null) {
            notificationService.sendSms(new MobileNoRequestDto(authUser.getMobileNo().getCountryCode(),
                            authUser.getMobileNo().getLocalNumber()),
                    dealApprovalStatusUpdateRequestDto.getApprovalStatus());
        }
    }
}
