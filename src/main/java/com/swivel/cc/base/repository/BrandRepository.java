package com.swivel.cc.base.repository;

import com.swivel.cc.base.domain.entity.Brand;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Set;

public interface BrandRepository extends JpaRepository<Brand, String> {

    /**
     * Get brand page by searching on name
     *
     * @param pageable pageable
     * @param name     name
     * @return Brand page
     */
    Page<Brand> findAllByNameContaining(Pageable pageable, String name);


    /**
     * This method returns a brand page by set of brand id with search term.
     *
     * @param pageable   pageable
     * @param ids        ids set
     * @param searchTerm search term
     * @return Brand Page
     */
    @Query("select b from Brand b where b.id in :ids and b.name like %:searchTerm%")
    Page<Brand> findByBrandIdsBySearch(Pageable pageable, Set<String> ids, String searchTerm);


    /**
     * This method returns a brand page by set of brand id(s).
     *
     * @param pageable pageable
     * @param ids      ids set
     * @return Brand Page
     */
    @Query("select b from Brand b where b.id in :ids")
    Page<Brand> findByBrandIds(Pageable pageable, Set<String> ids);

    /**
     * This method returns true when brand name exist in database.
     *
     * @param name name
     * @return true/ false
     */
    boolean existsByName(String name);

    /**
     * This method returns true when updating name exist with other brand.
     *
     * @param name brand name
     * @param id   brand id
     * @return ture/ false
     */
    boolean existsByNameAndIdNot(String name, String id);
}

