package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * Deal view count response
 */
@Getter
@Setter
@AllArgsConstructor
public class DealViewCountResponse {

    private Deal deal;
    private String merchantId;
    private long viewCount;
    private String displayDate;
}
