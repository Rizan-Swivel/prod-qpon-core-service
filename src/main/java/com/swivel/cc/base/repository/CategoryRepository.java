package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface CategoryRepository extends JpaRepository<Category, String> {

    /**
     * This method returns popular category page
     *
     * @param pageable  pageable
     * @param isPopular isPopular
     * @return category page
     */
    Page<Category> findAllByIsPopular(Pageable pageable, boolean isPopular);

    /**
     * This method returns categories search by name
     *
     * @param pageable pageable
     * @param name     category name
     * @return category page
     */
    Page<Category> findAllByNameContaining(Pageable pageable, String name);


    /**
     * This method returns a category page by set of category id with search term.
     *
     * @param pageable   pageable
     * @param ids        ids set
     * @param searchTerm search term
     * @return category page
     */
    @Query("select c from Category c where c.id in :ids and c.name like %:searchTerm%")
    Page<Category> findByCategoryIdsBySearch(Pageable pageable, Set<String> ids, String searchTerm);


    /**
     * This method returns a category page by set of category id.
     *
     * @param pageable pageable
     * @param ids      ids set
     * @return category page
     */
    @Query("select c from Category c where c.id in :ids")
    Page<Category> findByCategoryIds(Pageable pageable, Set<String> ids);


    /**
     * This method returns true when category name exist in database.
     *
     * @param name name
     * @return true/ false
     */
    boolean existsByName(String name);


    /**
     * This method returns true when updating name exist with other category.
     *
     * @param name category name
     * @param id   category id
     * @return true/ false
     */
    boolean existsByNameAndIdNot(String name, String id);

    /**
     * This method returns a category id set.
     *
     * @param categoryId category id
     * @return String set
     */
    @Query(value = "select c.related_category_id from category_relations c where c.category_id=:categoryId", nativeQuery = true)
    Set<String> getRelatedCategoriesById(String categoryId);

    /**
     * This method returns true when category is a relatedCategory for another category.
     *
     * @param category category
     * @return true/ false
     */
    boolean existsCategoryByRelatedCategories(Category category);

    /**
     * This method is used to get total number of active categories.
     *
     * @param currentTimeInMillis currentTimeInMillis
     * @return total number of active categories.
     */
    int countByExpiryDateIsNullOrExpiryDateGreaterThan(Long currentTimeInMillis);
}
