package com.swivel.cc.base.util;

import org.springframework.stereotype.Component;

/**
 * This class is used to validate fields.
 */
@Component
public class Validator {

    private static final String EMAIL_REGEX = "^[\\w-_.+]*[\\w-_.]@([\\w]+\\.)+[\\w]+[\\w]$";
    private static final String MOBILE_NO_REGEX = "^\\+(\\d{2}[-])\\d{9}$";
    private static final String NIC_REGEX = "^(?:19|20)?\\d{2}[0-9]{10}|[0-9]{9}[x|X|v|V]$";

    /**
     * This method validates a given email.
     *
     * @param email email address
     * @return true/ false
     */
    public boolean isValidEmail(String email) {
        return email != null && !email.isBlank() && email.trim().matches(EMAIL_REGEX);
    }

    /**
     * This method validates a given mobile no with country code.
     *
     * @param mobileNo mobile no
     * @return true/ false
     */
    public boolean isValidMobileNoWithCountryCode(String mobileNo) {
        return mobileNo.matches(MOBILE_NO_REGEX);
    }

    /**
     * This method validates a given nic number.
     *
     * @param nic nic number
     * @return true/ false
     */
    public boolean isValidNIC(String nic) {
        return nic != null && !nic.isBlank() && nic.trim().matches(NIC_REGEX);
    }
}
