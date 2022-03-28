package com.swivel.cc.base.domain.response;

import lombok.Getter;
import lombok.Setter;

/**
 * This dto get the response from report v4 api
 */
@Setter
@Getter
public class ViewCountAnalyticResponseDto {

    private String dealId;
    private String merchantId;
    private String categoryId;
    private long viewCount;
    private String displayDate;


    public ViewCountAnalyticResponseDto(String dealId, String merchantId) {
        this.dealId = dealId;
        this.merchantId = merchantId;
    }

    public ViewCountAnalyticResponseDto(String categoryId, long viewCount) {
        this.categoryId = categoryId;
        this.viewCount = viewCount;
    }
}
