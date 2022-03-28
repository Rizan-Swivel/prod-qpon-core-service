package com.swivel.cc.base.configuration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.stereotype.Component;

import java.util.Locale;

/**
 * Build messages according to the language
 */
@Component
public class Translator {

    private final ResourceBundleMessageSource messageSource;
    private final MessageSourceAccessor messageSourceAccessor;

    @Autowired
    public Translator(ResourceBundleMessageSource messageSource) {
        this.messageSource = messageSource;
        this.messageSourceAccessor = new MessageSourceAccessor(messageSource, Locale.ENGLISH);
    }

    /**
     * Return the message based on the language
     *
     * @param msgCode msgCode
     * @return message
     */
    public String toLocale(String msgCode) {
        var locale = LocaleContextHolder.getLocale();
        return messageSource.getMessage(msgCode, null, locale);
    }

    /**
     * Return default locale messages
     *
     * @param msgCode msgCode
     * @return message
     */
    public String toDefaultLocale(String msgCode) {
        return messageSourceAccessor.getMessage(msgCode);
    }

    /**
     * Return the language tag
     *
     * @return language tag
     */
    public String getLanguageTag() {
        return LocaleContextHolder.getLocale().toLanguageTag();
    }
}