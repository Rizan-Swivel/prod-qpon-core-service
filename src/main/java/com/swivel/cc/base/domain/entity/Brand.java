package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.request.BrandRequestDto;
import com.swivel.cc.base.domain.request.BrandUpdateRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.UUID;

/**
 * Brand entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "brand")
public class Brand {
    @Transient
    private static final String BRAND_ID_PREFIX = "bid-";

    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private String imageUrl;
    private long createdAt;
    private long updatedAt;

    public Brand(BrandRequestDto brandRequestDto) {
        this.id = BRAND_ID_PREFIX + UUID.randomUUID();
        this.name = brandRequestDto.getName();
        this.description = brandRequestDto.getDescription();
        this.imageUrl = brandRequestDto.getImageUrl();
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
    }

    public void update(BrandUpdateRequestDto brandUpdateRequestDto) {
        this.name = brandUpdateRequestDto.getName();
        this.description = brandUpdateRequestDto.getDescription();
        this.imageUrl = brandUpdateRequestDto.getImageUrl();
        this.updatedAt = System.currentTimeMillis();
    }

}
