package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchant;
import com.swivel.cc.base.domain.entity.CategoryBrandMerchantIndex;
import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidBrandException;
import com.swivel.cc.base.exception.InvalidCategoryException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.CategoryBrandMerchantRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Categories for merchant service
 */
@Service
public class CategoryBrandMerchantService {

    private static final String ALL = "ALL";
    private static final String READ_BRANDS_CATEGORIES = "Reading brandsCategoriesForMerchant from the database" +
            " was failed.";
    private final CategoryBrandMerchantRepository categoryBrandMerchantRepository;
    private final CategoryBrandMerchantIndexService categoryBrandMerchantIndexService;
    private final CategoryService categoryService;
    private final BrandService brandService;

    @Autowired
    public CategoryBrandMerchantService(CategoryBrandMerchantRepository categoryBrandMerchantRepository,
                                        CategoryBrandMerchantIndexService categoryBrandMerchantIndexService,
                                        CategoryService categoryService, BrandService brandService) {
        this.categoryBrandMerchantRepository = categoryBrandMerchantRepository;
        this.categoryBrandMerchantIndexService = categoryBrandMerchantIndexService;
        this.categoryService = categoryService;
        this.brandService = brandService;
    }


    /**
     * This method create brands and categories mapping for merchant
     *
     * @param categoryBrandMerchant            categoryBrandMerchant
     * @param basicMerchantBusinessResponseDto basicMerchantBusinessResponseDto
     * @return CategoryBrandMerchant
     */
    public CategoryBrandMerchant createCategoriesForMerchant(CategoryBrandMerchant categoryBrandMerchant,
                                                             BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto) {
        try {
            categoryBrandMerchantIndexService.save(new CategoryBrandMerchantIndex(categoryBrandMerchant,
                    basicMerchantBusinessResponseDto));
            categoryBrandMerchant.setActiveMerchant(basicMerchantBusinessResponseDto.isActive());
            return categoryBrandMerchantRepository.save(categoryBrandMerchant);

        } catch (DataAccessException e) {
            throw new QponCoreException("Saving category merchant into database was failed.", e);
        }
    }

    /**
     * This method returns CategoryBrandMerchant by merchant id.
     *
     * @param merchantId merchantId
     * @return optional CategoryBrandMerchant
     */
    public Optional<CategoryBrandMerchant> getBrandsCategoryMapperByMerchantId(String merchantId) {
        try {
            return categoryBrandMerchantRepository.findByMerchantId(merchantId);
        } catch (DataAccessException e) {
            throw new QponCoreException(READ_BRANDS_CATEGORIES, e);
        }
    }

    /**
     * This method return merchants Id list.
     *
     * @param categoryId         categoryId
     * @param onlyActiveMerchant onlyActiveMerchant ture or false
     * @return MerchantListResponseDto
     */
    public List<String> findAllMerchantsByCategoryId(
            Pageable pageable,
            String categoryId,
            boolean onlyActiveMerchant) {
        try {
            Page<CategoryBrandMerchant> categoryMerchants = null;

            if (onlyActiveMerchant) {
                categoryMerchants = categoryId.equals(ALL) ?
                        categoryBrandMerchantRepository.findAllByIsActiveMerchant(pageable, true) :
                        categoryBrandMerchantRepository.findAllByCategoriesAndIsActiveMerchant(pageable, categoryId, true);
            } else {
                categoryMerchants = categoryId.equals(ALL) ?
                        categoryBrandMerchantRepository.findAll(pageable) :
                        categoryBrandMerchantRepository.findAllByCategories(pageable, categoryId);
            }

            List<String> merchantIds = new ArrayList<>();
            categoryMerchants.forEach(categoryBrandMerchant -> merchantIds.add(categoryBrandMerchant.getMerchantId()));
            return merchantIds;
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading merchantIds from database was failed.", e);
        }
    }

    /**
     * Update CategoriesBrandsOfMerchant and CategoriesBrandsOfMerchantIndex
     *
     * @param categoryBrandMerchant categoryBrandMerchant
     */
    public CategoryBrandMerchant updateCategoriesBrandsOfMerchant(CategoryBrandMerchant categoryBrandMerchant,
                                                                  BasicMerchantBusinessResponseDto
                                                                          basicMerchantBusinessResponseDto) {
        try {

            Optional<CategoryBrandMerchant> optionalCategoryMerchant =
                    categoryBrandMerchantRepository.findByMerchantId(categoryBrandMerchant.getMerchantId());
            if (optionalCategoryMerchant.isPresent()) {
                var categoryBrandMerchantFromDb = optionalCategoryMerchant.get();
                categoryBrandMerchantFromDb.getBrands().clear();
                categoryBrandMerchantFromDb.getCategories().clear();
                categoryBrandMerchantFromDb.getBrands().addAll(categoryBrandMerchant.getBrands());
                categoryBrandMerchantFromDb.getCategories().addAll(categoryBrandMerchant.getCategories());
                categoryBrandMerchantFromDb.setUpdatedAt(System.currentTimeMillis());
                categoryBrandMerchantFromDb
                        .setActiveMerchant(basicMerchantBusinessResponseDto.isActive());
                categoryBrandMerchantIndexService.updateCategoryBrandMerchantIndexByMerchantId(
                        categoryBrandMerchant.getMerchantId(),
                        categoryBrandMerchant.getBrands().toString(),
                        categoryBrandMerchant.getCategories().toString(),
                        basicMerchantBusinessResponseDto.isActive(),
                        basicMerchantBusinessResponseDto.getApprovalStatus());
                return categoryBrandMerchantRepository.save(categoryBrandMerchantFromDb);
            } else {
                return createCategoriesForMerchant(categoryBrandMerchant, basicMerchantBusinessResponseDto);
            }
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating Categories & brands of merchant in database was failed.", e);
        }
    }

    /**
     * This method return number of merchants for specific brand
     *
     * @param brandId  brandId
     * @param userType BANK/MERCHANT
     * @return count merchants Count By BrandId
     */
    public long getMerchantCountByBrandIdAndUserType(String brandId, UserType userType) {
        try {
            return categoryBrandMerchantRepository.countDistinctMerchantIdByBrandsAndUserType(brandId, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading merchant count from the database by Brand id was failed", e);
        }

    }


    /**
     * This method returns count of merchant for specific category id
     *
     * @param categoryId category id
     * @param userType   BANK/MERCHANT
     * @return count merchants Count By Category id
     */
    public long getMerchantCountByCategoryIdAndUserType(String categoryId, UserType userType) {

        try {
            return categoryBrandMerchantRepository.countDistinctMerchantIdByCategoriesAndUserType(categoryId, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading merchant count from the database by category id was failed", e);
        }
    }

    /**
     * This method will return active merchant count for category id.
     *
     * @param categoryId category id
     * @param userType   BANK/MERCHANT
     * @return active merchant count
     */
    public long getActiveMerchantCountByCategoryIdAndUserType(String categoryId, UserType userType) {
        try {
            return categoryBrandMerchantRepository
                    .countDistinctMerchantIdByCategoriesAndIsActiveMerchantTrueAndUserType(categoryId, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading active merchant count from the database by category id was failed", e);
        }
    }

    /**
     * This method will return active merchant count for brand id.
     *
     * @param brandId  brand id
     * @param userType BANK/MERCHANT
     * @return active merchant count
     */
    public long getActiveMerchantCountByBrandIdAndUserType(String brandId, UserType userType) {
        try {
            return categoryBrandMerchantRepository
                    .countDistinctMerchantIdByBrandsAndIsActiveMerchantTrueAndUserType(brandId, userType);
        } catch (DataAccessException e) {
            throw new QponCoreException("Reading active merchant count from the database by brand id was failed", e);
        }
    }

    /**
     * This method returns category page by category id set.
     *
     * @param pageable   pageable
     * @param ids        ids
     * @param searchTerm search term
     * @return Category page
     */
    public Page<Category> getCategoriesByIds(Pageable pageable, Set<String> ids, String searchTerm) {
        try {
            return categoryService.getBulkCategoriesByIds(pageable, ids, searchTerm);
        } catch (InvalidCategoryException e) {
            throw new InvalidBrandException("Invalid Category Id(s)");
        } catch (DataAccessException e) {
            throw new QponCoreException("Returning categories from the database was failed", e);
        }
    }

    /**
     * This method returns Brand page by category id set.
     *
     * @param pageable   pageable
     * @param ids        ids
     * @param searchTerm search term
     * @return Brand Page
     */
    public Page<Brand> getBrandsByIds(Pageable pageable, Set<String> ids, String searchTerm) {
        try {
            return brandService.getBulkBrandsByIds(pageable, ids, searchTerm);
        } catch (InvalidCategoryException e) {
            throw new InvalidBrandException("Invalid Category Id(s)");
        } catch (DataAccessException e) {
            throw new QponCoreException("Returning categories from the database was failed", e);
        }
    }

    /**
     * This method returns true when brand id mapped with categoryBrandMerchant.
     *
     * @param brandId brand id
     * @return true/ false
     */
    public boolean checkBrandIdMapWithCategoryBrandMerchant(String brandId) {
        try {
            return categoryBrandMerchantRepository.existsByBrands(brandId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Checking brand id is associated with categoryBrandMerchant" +
                    " from the database was failed", e);
        }
    }

    /**
     * This method returns true if specific category associate with categoryBrandMerchant.
     *
     * @param categoryId category id
     * @return true/ false
     */
    public boolean checkCategoryIdMapWithCategoryBrandMerchant(String categoryId) {
        try {
            return categoryBrandMerchantRepository.existsAllByCategories(categoryId);
        } catch (DataAccessException e) {
            throw new QponCoreException("Checking category id is associated with categoryBrandMerchant" +
                    " from the database was failed", e);
        }
    }

    /**
     * This method will update merchant info.
     *
     * @param merchantIds merchant id List
     * @param merchantMap merchant id, MerchantBusinessResponseDto Map
     */
    public void updateMerchantInfo(List<String> merchantIds, Map<String, MerchantBusinessResponseDto> merchantMap) {

        try {
            var categoryBrandMerchantList =
                    categoryBrandMerchantRepository.getAllByMerchantIdIn(merchantIds);
            for (var categoryBrandMerchant : categoryBrandMerchantList) {
                var merchant = merchantMap.get(categoryBrandMerchant.getMerchantId());
                categoryBrandMerchant.setMerchantApprovalStatus(merchant.getApprovalStatus());
                categoryBrandMerchant.setActiveMerchant(merchant.isActive());
            }
            categoryBrandMerchantRepository.saveAll(categoryBrandMerchantList);
        } catch (DataAccessException e) {
            throw new QponCoreException("Updating merchant info for categoryBrandMerchant was failed", e);
        }
    }
}
