package com.swivel.cc.base.domain.entity;

import com.swivel.cc.base.domain.request.CategoryRequestDto;
import com.swivel.cc.base.domain.request.CategoryUpdateRequestDto;
import com.swivel.cc.base.enums.CategoryType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Category entity
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "category")
public class Category {
    @Transient
    private static final String CATEGORY_ID_PREFIX = "cid-";

    @Id
    private String id;
    @Column(nullable = false)
    private String name;
    @Column(nullable = false)
    private String description;
    private String imageUrl;
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CategoryType categoryType;
    @ManyToMany()
    @JoinTable(
            name = "category_relations",
            joinColumns = @JoinColumn(name = "categoryId"),
            inverseJoinColumns = @JoinColumn(name = "relatedCategoryId"))
    private Set<Category> relatedCategories = new HashSet<>();
    private Long expiryDate = null;
    private long createdAt;
    private long updatedAt;
    private boolean isPopular;

    public Category(CategoryRequestDto categoryRequestDto, Set<Category> relatedCategories) {
        this.id = CATEGORY_ID_PREFIX + UUID.randomUUID();
        this.name = categoryRequestDto.getName();
        this.description = categoryRequestDto.getDescription();
        this.imageUrl = categoryRequestDto.getImageUrl();
        this.categoryType = categoryRequestDto.getCategoryType();
        this.expiryDate = categoryType == CategoryType.SEASONAL ? categoryRequestDto.getExpiryDate() : null;
        this.createdAt = System.currentTimeMillis();
        this.updatedAt = System.currentTimeMillis();
        this.relatedCategories = relatedCategories;
        this.isPopular = categoryRequestDto.isPopular();
    }

    public void update(CategoryUpdateRequestDto categoryUpdateRequestDto, Set<Category> relatedCategories) {
        Set<Category> filteredRelatedCategories = relatedCategories
                .stream()
                .filter(category -> category.getId() != categoryUpdateRequestDto.getId())
                .collect(Collectors.toSet());
        this.name = categoryUpdateRequestDto.getName();
        this.description = categoryUpdateRequestDto.getDescription();
        this.imageUrl = categoryUpdateRequestDto.getImageUrl();
        this.categoryType = categoryUpdateRequestDto.getCategoryType();
        this.relatedCategories = filteredRelatedCategories;
        this.expiryDate = categoryUpdateRequestDto.getExpiryDate();
        this.updatedAt = System.currentTimeMillis();
        this.isPopular = categoryUpdateRequestDto.isPopular();
    }
}
