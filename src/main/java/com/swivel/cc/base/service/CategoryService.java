package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.request.CategoryUpdateRequestDto;
import com.swivel.cc.base.domain.response.CategoryResponseDto;
import com.swivel.cc.base.domain.response.CategoryViewCountResponseDto;
import com.swivel.cc.base.domain.response.ViewCountAnalyticResponseDto;
import com.swivel.cc.base.enums.CategoryType;
import com.swivel.cc.base.exception.InvalidCategoryException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.exception.UnsupportedCategoryTypeException;
import com.swivel.cc.base.exception.UnsupportedDeleteAction;
import com.swivel.cc.base.repository.CategoryRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CategoryService {

    private static final String ALL = "ALL";
    private static final String INVALID_CATEGORY = "Invalid category Id: ";
    private final CategoryRepository categoryRepository;
    private final CategoryBrandMerchantService categoryBrandMerchantService;

    @Autowired
    public CategoryService(CategoryRepository categoryRepository,
                           @Lazy CategoryBrandMerchantService categoryBrandMerchantService) {
        this.categoryRepository = categoryRepository;
        this.categoryBrandMerchantService = categoryBrandMerchantService;
    }

    /**
     * Get list of categories
     *
     * @param pageable pageable with page, size and sort
     * @return category page
     */
    public Page<Category> listAllCategories(Pageable pageable, String searchTerm) {
        try {
            if (searchTerm.equals("ALL"))
                return categoryRepository.findAll(pageable);
            else
                return categoryRepository.findAllByNameContaining(pageable, searchTerm);

        } catch (DataAccessException e) {
            throw new QponCoreException("Reading category list from database was failed.", e);
        }
    }

    /**
     * Get list of popular categories
     *
     * @param pageable pageable with page, size and sort
     * @return category page
     */
    public Page<Category> listAllPopularCategories(Pageable pageable) {
        try {
            return categoryRepository.findAllByIsPopular(pageable, true);

        } catch (DataAccessException e) {
            throw new QponCoreException("Reading category list from database was failed.", e);
        }
    }

    /**
     * Read categories by Ids
     *
     * @param categoryIdList categoryIdList
     * @return Category set
     */
    public Set<Category> getCategorySetByIds(List<String> categoryIdList) {
        try {
            Set<Category> relatedCategories = new HashSet<>();
            for (String id : categoryIdList) {
                Optional<Category> optionalCategoryFromDb = categoryRepository.findById(id);
                if (optionalCategoryFromDb.isPresent()) {
                    relatedCategories.add(optionalCategoryFromDb.get());
                } else {
                    throw new InvalidCategoryException(INVALID_CATEGORY + id);
                }
            }
            return relatedCategories;
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading category by id from database was failed.", e);
        }
    }

    /**
     * Get category by Id
     *
     * @param categoryId category Id
     * @return category
     */
    public Category getCategoryById(String categoryId) {
        try {
            Optional<Category> optionalCategoryFromDb = categoryRepository.findById(categoryId);
            if (optionalCategoryFromDb.isPresent()) {
                return optionalCategoryFromDb.get();
            } else {
                throw new InvalidCategoryException(INVALID_CATEGORY + categoryId);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading category from database was failed.", e);
        }
    }

    /**
     * Save new category into database
     *
     * @param category category
     */
    public void createCategory(Category category) {
        try {
            categoryRepository.save(category);
        } catch (DataAccessException e) {
            throw new QponCoreException("Saving category into database was failed.", e);
        }
    }

    /**
     * This method validate the given categoryId.
     *
     * @param categoryId categoryId
     */
    public void validateCategoryId(String categoryId) {
        Optional<Category> categoryFromDb = categoryRepository.findById(categoryId);
        if (categoryFromDb.isEmpty()) {
            throw new InvalidCategoryException(INVALID_CATEGORY + categoryId);
        }
    }

    /**
     * This method update the existing category
     *
     * @param categoryUpdateRequestDto categoryUpdateRequestDto
     * @return category
     */
    public Category updateCategory(CategoryUpdateRequestDto categoryUpdateRequestDto) {
        try {
            var categoryFromDb = getCategoryById(categoryUpdateRequestDto.getId());
            var categorySet = getCategorySetByIds(categoryUpdateRequestDto.getRelatedCategories());
            categoryFromDb.update(categoryUpdateRequestDto, categorySet);
            return categoryRepository.save(categoryFromDb);
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating category into database was failed.", e);
        }
    }

    /**
     * This method returns a category page by category ids set.
     *
     * @param pageable pageable
     * @param ids      category id set
     * @return category page
     */
    public Page<Category> getBulkCategoriesByIds(Pageable pageable, Set<String> ids, String searchTerm) {
        try {
            return ((ALL.equals(searchTerm)) ? categoryRepository.findByCategoryIds(pageable, ids) :
                    categoryRepository.findByCategoryIdsBySearch(pageable, ids, searchTerm));

        } catch (DataAccessException e) {
            throw new QponCoreException("Returning bulk Category by Id(s) from database was failed.", e);
        }
    }

    /**
     * This method returns true when category name is exist in the database.
     *
     * @param categoryName category name
     * @return true/ false
     */
    public boolean checkCategoryNameExist(String categoryName) {
        try {
            return categoryRepository.existsByName(categoryName);
        } catch (DataAccessException e) {
            throw new QponCoreException("Checking category Name is exists from the database was failed.", e);
        }
    }

    /**
     * This method returns true when updating name exist with other category.
     *
     * @param id   category id
     * @param name category name
     * @return true/ false
     */
    public boolean checkCategoryNameNotExistForOtherIDs(String id, String name) {
        try {
            return categoryRepository.existsByNameAndIdNot(name, id);
        } catch (DataAccessException e) {
            throw new QponCoreException(
                    "Checking category Name is exists for other id(s) from the database was failed.", e);
        }
    }

    /**
     * This method deletes a category.
     *
     * @param categoryId categoryId
     */
    public void deleteCategory(String categoryId) {
        try {
            var category = getCategoryById(categoryId);
            boolean isCategoryMapWithCategoryBrandMerchant = categoryBrandMerchantService
                    .checkCategoryIdMapWithCategoryBrandMerchant(categoryId);
            boolean isCategoryARelatedCategory = checkCategoryInRelatedCategories(category);
            if (!isCategoryMapWithCategoryBrandMerchant && !isCategoryARelatedCategory) {
                categoryRepository.delete(category);
            } else {
                throw new UnsupportedDeleteAction("Category cannot be deleted.");
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Deleting Category from the database was failed.", e);
        }
    }

    /**
     * This method returns true when category is a relatedCategory for another category.
     *
     * @param category category
     * @return true/ false
     */
    public boolean checkCategoryInRelatedCategories(Category category) {
        try {
            return categoryRepository.existsCategoryByRelatedCategories(category);
        } catch (DataAccessException e) {
            throw new QponCoreException(
                    "Checking category is exists in related categories from the database was failed.", e);
        }
    }

    /**
     * This method used to get page of related categories for a category.
     *
     * @param pageable   pageable
     * @param categoryId category id
     * @return Category page
     */
    public Page<Category> getRelatedCategoryListForCategory(Pageable pageable, String categoryId) {
        try {
            Category category = getCategoryById(categoryId);
            if (category.getCategoryType().equals(CategoryType.SEASONAL)) {
                Set<String> relatedCategoriesById = categoryRepository.getRelatedCategoriesById(categoryId);
                return getBulkCategoriesByIds(pageable, relatedCategoriesById, ALL);
            } else {
                throw new UnsupportedCategoryTypeException("Invalid Category for retuning related categories.");
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Returning related categories for a category id from the database was failed.", e);
        }
    }

    /**
     * This method is used to get categories for category report.
     *
     * @param categoryViewsList categoryViewsList
     * @return categories with view count.
     */
    public List<CategoryViewCountResponseDto> getCategoriesForReport(List<ViewCountAnalyticResponseDto>
                                                                             categoryViewsList, String timeZone) {
        try {
            List<CategoryViewCountResponseDto> categoryAndViews = new ArrayList<>();
            for (ViewCountAnalyticResponseDto categoryIdAndViews : categoryViewsList) {
                Optional<Category> optionalCategory = categoryRepository.findById(categoryIdAndViews.getCategoryId());
                optionalCategory.ifPresent(category -> categoryAndViews.add(
                        new CategoryViewCountResponseDto(new CategoryResponseDto(category, timeZone),
                                categoryIdAndViews.getViewCount(), categoryIdAndViews.getDisplayDate())));
            }
            return categoryAndViews;
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading deal by id from database was failed.", e);
        }
    }
}
