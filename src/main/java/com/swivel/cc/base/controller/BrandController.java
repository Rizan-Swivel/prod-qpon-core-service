package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.request.BrandRequestDto;
import com.swivel.cc.base.domain.request.BrandUpdateRequestDto;
import com.swivel.cc.base.domain.response.BasicBrandListResponseDto;
import com.swivel.cc.base.domain.response.BrandDetailResponseDto;
import com.swivel.cc.base.domain.response.BrandListResponseDto;
import com.swivel.cc.base.domain.response.BrandUpsertResponseDto;
import com.swivel.cc.base.enums.ErrorResponseStatusType;
import com.swivel.cc.base.enums.SuccessResponseStatusType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidBrandException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.exception.UnsupportedDeleteAction;
import com.swivel.cc.base.service.BrandService;
import com.swivel.cc.base.service.CategoryBrandMerchantService;
import com.swivel.cc.base.service.DealSearchService;
import com.swivel.cc.base.wrapper.ResponseWrapper;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.Positive;

/**
 * Brand Controller
 */
@CrossOrigin
@Slf4j
@RestController
@Validated
@RequestMapping("api/v1/brands")
public class BrandController extends Controller {

    private final BrandService brandService;
    private final CategoryBrandMerchantService categoryBrandMerchantService;
    private final DealSearchService dealSearchService;
    Logger logger = LoggerFactory.getLogger(BrandController.class);

    @Autowired
    public BrandController(BrandService brandService, CategoryBrandMerchantService categoryBrandMerchantService,
                           Translator translator, DealSearchService dealSearchService) {
        super(translator);
        this.brandService = brandService;
        this.categoryBrandMerchantService = categoryBrandMerchantService;
        this.dealSearchService = dealSearchService;
    }

    /**
     * Get paginated brand list
     *
     * @return Brand List Response
     */
    @GetMapping("/{page}/{size}/search/{searchTerm}")
    public ResponseEntity<ResponseWrapper> getBrandList(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size,
            @PathVariable String searchTerm) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            Page<Brand> brandPage = brandService.listAllBrands(pageable, searchTerm);
            var brandListResponseDto = new BrandListResponseDto(brandPage, timeZone);
            logger.debug("Successfully return the brand list. userId: {}, page: {}, size {}",
                    userId, page, size);
            return getSuccessResponse(brandListResponseDto, SuccessResponseStatusType.READ_BRAND_LIST);
        } catch (QponCoreException e) {
            logger.error("Returning brand list was failed. userId: {}, page: {}, size {}",
                    userId, page, size, e);
            return getInternalServerError();
        }
    }

    /**
     * Get paginated basic brand list
     *
     * @return Basic Brand List Response
     */
    @GetMapping("/basic/{page}/{size}")
    public ResponseEntity<ResponseWrapper> getBasicBrandList(
            @Min(DEFAULT_PAGE) @PathVariable int page,
            @Positive @Max(PAGE_MAX_SIZE) @PathVariable int size) {
        try {
            Pageable pageable = PageRequest.of(page, size, Sort.by(DEFAULT_SORT).descending());
            Page<Brand> brandPage = brandService.listAllBrands(pageable, ALL);
            var basicBrandListResponseDto = new BasicBrandListResponseDto(brandPage);
            logger.debug("Successfully return the basic brand list. page: {}, size {}",
                    page, size);
            return getSuccessResponse(basicBrandListResponseDto, SuccessResponseStatusType.READ_BRAND_LIST);
        } catch (QponCoreException e) {
            logger.error("Returning basic brand list was failed. page: {}, size {}",
                    page, size, e);
            return getInternalServerError();
        }
    }


    @Secured({ADMIN_ROLE})
    @PostMapping("")
    public ResponseEntity<ResponseWrapper> createBrand(
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestBody BrandRequestDto brandRequestDto) {
        try {
            if (!brandRequestDto.isRequiredAvailable()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            boolean isBrandNameExist = brandService.checkBrandNameExist(brandRequestDto.getName());
            if (isBrandNameExist) {
                return getErrorResponse(ErrorResponseStatusType.EXISTING_BRAND_NAME);
            }
            var brandCreateResponseDto = createBrandResponseDtoEntity(brandRequestDto, timeZone);
            String objectToJson = brandCreateResponseDto.toLogJson();
            logger.debug("Successfully create brand. userId: {}, brand response {}",
                    userId, objectToJson);
            return getSuccessResponse(brandCreateResponseDto,
                    SuccessResponseStatusType.CREATE_BRAND);
        } catch (QponCoreException e) {
            logger.error("Creating new brand was failed. userId: {}", userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Get Brand Detail by BrandId
     *
     * @param userId   user id
     * @param timeZone time zone
     * @param brandId  brand id
     * @return BrandResponseDto
     */
    @GetMapping("/{brandId}")
    public ResponseEntity<ResponseWrapper> getDetailBrand(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String brandId) {

        try {
            var brand = brandService.getBrandById(brandId);
            logger.info("brand details: {}", brand);
            var dealsCount = dealSearchService.getDealsCountByBrandId(brandId);
            var activeDealsCountByBrandId = dealSearchService.getActiveDealsCountByBrandId(brandId);
            var merchantCount = categoryBrandMerchantService
                    .getMerchantCountByBrandIdAndUserType(brandId, UserType.MERCHANT);
            var activeMerchantCount = categoryBrandMerchantService
                    .getActiveMerchantCountByBrandIdAndUserType(brandId, UserType.MERCHANT);
            var bankCount = categoryBrandMerchantService
                    .getMerchantCountByBrandIdAndUserType(brandId, UserType.BANK);
            var activeBankCount = categoryBrandMerchantService
                    .getActiveMerchantCountByBrandIdAndUserType(brandId, UserType.BANK);
            var brandDetailResponseDto =
                    new BrandDetailResponseDto(brand,
                            merchantCount, dealsCount, timeZone, activeDealsCountByBrandId, activeMerchantCount);
            brandDetailResponseDto.setNoOfBanks(bankCount);
            brandDetailResponseDto.setNoOfActiveBanks(activeBankCount);
            return getSuccessResponse(brandDetailResponseDto, SuccessResponseStatusType.READ_BRAND_DETAIL);
        } catch (InvalidBrandException e) {
            logger.error("Invalid brandId: {} to request brand detail for user Id: {}", brandId, userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            logger.error("Failed to return brand detail by brandId: {}, userId: {}", brandId, userId, e);
            return getInternalServerError();
        }
    }

    /**
     * Update existing brand.
     *
     * @param timeZone              time zone
     * @param userId                user id
     * @param brandUpdateRequestDto brandUpdateRequestDto
     * @return brandCreateUpdateResponse
     */
    @Secured({ADMIN_ROLE})
    @PutMapping("")
    public ResponseEntity<ResponseWrapper> updateBrand(
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestBody BrandUpdateRequestDto brandUpdateRequestDto) {
        try {
            if (!brandUpdateRequestDto.isRequiredAvailable()) {
                return getErrorResponse(ErrorResponseStatusType.MISSING_REQUIRED_FIELDS);
            }
            boolean isBrandNameExist = brandService
                    .checkBrandNameNotExistForOtherIDs(brandUpdateRequestDto.getId(), brandUpdateRequestDto.getName());
            if (isBrandNameExist) {
                return getErrorResponse(ErrorResponseStatusType.EXISTING_BRAND_NAME);
            }
            var updateBrand = brandService.updateBrand(brandUpdateRequestDto);
            var brandResponseDto = new BrandUpsertResponseDto(updateBrand, timeZone);
            String brandResponseLogJson = brandResponseDto.toLogJson();
            logger.debug("Successfully updated the brand. userId: {}, brand response {}",
                    userId, brandResponseLogJson);
            return getSuccessResponse(brandResponseDto, SuccessResponseStatusType.UPDATE_BRAND);
        } catch (InvalidBrandException e) {
            logger.error("Invalid brandId: {} to update brand detail for user Id: {}",
                    brandUpdateRequestDto.getId(), userId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (QponCoreException e) {
            logger.error("Updating existing brand was failed. userId: {}", userId, e);
            return getInternalServerError();
        }
    }

    /**
     * this method convert BrandRequestDto into BrandCreateResponseDto
     *
     * @param brandRequestDto brandRequestDto
     * @return BrandCreateResponseDto
     */
    private BrandUpsertResponseDto createBrandResponseDtoEntity(BrandRequestDto brandRequestDto,
                                                                String timeZone) {
        var brand = new Brand(brandRequestDto);
        brandService.createBrand(brand);

        return new BrandUpsertResponseDto(brand, timeZone);
    }

    /**
     * Delete brand permanently when category is not map with categoryBrandMerchant and relatedBrand.
     *
     * @param userId   user id
     * @param timeZone time zone
     * @param brandId  brand id
     * @return SuccessResponse/ ErrorResponse
     */
    @DeleteMapping("/delete/{brandId}")
    public ResponseEntity<ResponseWrapper> deleteBrand(
            @RequestHeader(name = USER_ID_HEADER) String userId,
            @RequestHeader(name = TIME_ZONE_HEADER) String timeZone,
            @PathVariable String brandId) {
        try {
            brandService.deleteBrand(brandId);
            return getSuccessResponse(null, SuccessResponseStatusType.DELETE_BRAND);
        } catch (InvalidBrandException e) {
            logger.error("Invalid brandId: {} to delete.", brandId, e);
            return getErrorResponse(ErrorResponseStatusType.INVALID_BRAND_ID);
        } catch (UnsupportedDeleteAction e) {
            logger.error("Brand cannot be deleted by brandId: {}" +
                    " because it is already mapped with merchants ", brandId, e);
            return getErrorResponse(ErrorResponseStatusType.UNSUPPORTED_BRAND_DELETE);
        } catch (QponCoreException e) {
            logger.error("Deleting the brand was failed for brandId: {} with userId: {}.", brandId, userId, e);
            return getInternalServerError();
        }
    }
}
