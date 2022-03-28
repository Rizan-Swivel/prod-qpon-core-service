package com.swivel.cc.base.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.Id;


@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CombinedDealRequest {

    private String categoryId;
    private String categoryName;
    private String brandId;
    private String brandName;
    @Id
    private String merchantId;
    private String merchantName;
    private long count;

}
