package com.swivel.cc.base.domain.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Mail request dto
 */
@AllArgsConstructor
@Setter
@Getter
public class MailRequestDto extends RequestDto {

    private final String recipientAddress;
    private final String title;
    private final String content;

    @Override
    public String toLogJson() {
        return toJson();
    }

    @Override
    public boolean isRequiredAvailable() {
        return isNonEmpty(recipientAddress) && isNonEmpty(title) && isNonEmpty(content);
    }
}
