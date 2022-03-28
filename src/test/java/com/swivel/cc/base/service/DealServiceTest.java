package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.Brand;
import com.swivel.cc.base.domain.entity.Category;
import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.domain.request.DealRequestDto;
import com.swivel.cc.base.domain.response.BasicMerchantBusinessResponseDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.CategoryType;
import com.swivel.cc.base.enums.DeductionType;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidDealException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.BankDealRepository;
import com.swivel.cc.base.repository.DealRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.dao.DataAccessException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

/**
 * This class tests the {@link DealService } class.
 */
class DealServiceTest {

    private static final String DEAL_CODE = "did-92be0c67-3810-47c2-9e28-615f81efad6a";
    private static final String INVALID_DEAL = "Invalid deal Id: did-92be0c67-3810-47c2-9e28-615f81efad6a";
    private DealService dealService;
    @Mock
    private DealRepository dealRepository;
    @Mock
    private DealSearchService dealSearchService;
    @Mock
    private DealApprovalService dealApprovalService;
    @Mock
    private BrandService brandService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private BankDealRepository bankDealRepository;
    @Mock
    private MerchantBankSearchIndexService merchantBankSearchIndexService;
    @Mock
    private DealCodeService dealCodeService;


    @BeforeEach
    void setUp() {
        initMocks(this);
        dealService = new DealService(dealRepository, bankDealRepository, dealSearchService, dealApprovalService,
                brandService, categoryService, merchantBankSearchIndexService, dealCodeService);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_SaveTheDeal() {
        BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto = getBasicMerchantBusinessResponseDto();
        BusinessMerchantResponseDto businessMerchantResponseDto = getSampleBusinessMerchantResponseDto();
        Deal deal = getDeal();
        dealService.createDeal(deal, basicMerchantBusinessResponseDto,
                UserType.MERCHANT, getDealRequestDto(), businessMerchantResponseDto);
        verify(dealRepository, times(1)).save(deal);
    }

    @Test
    void Should_ThrowQponCoreException_when_SavingDealWithEmptyId() {
        BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto = getBasicMerchantBusinessResponseDto();
        BusinessMerchantResponseDto businessMerchantResponseDto = getSampleBusinessMerchantResponseDto();
        Deal deal = getDeal();
        when(dealRepository.save(deal)).thenThrow(new DataAccessException("FAILD") {
        });
        QponCoreException qponCoreException = assertThrows(QponCoreException.class, () -> {
            dealService.createDeal(deal, basicMerchantBusinessResponseDto,
                    UserType.MERCHANT, getDealRequestDto(), businessMerchantResponseDto);
        });
        assertEquals("Saving deal to database was failed", qponCoreException.getMessage());
    }

    @Test
    void Should_DeleteDeal_When_OptionalDealPresent() {
        Deal deal = getDeal();
        when(dealRepository.findPendingDealById(DEAL_CODE)).thenReturn(Optional.of(deal));
        dealService.deleteDeal(DEAL_CODE, UserType.MERCHANT);
        verify(dealRepository, times(1)).save(deal);
    }

    @Test
    void Should_ThrowException_When_WhileDeletingADeal() {
        when(dealRepository.findPendingDealById(DEAL_CODE)).thenReturn(Optional.ofNullable(null));
        InvalidDealException invalidDealException = assertThrows(InvalidDealException.class, () -> {
            dealService.deleteDeal(DEAL_CODE, UserType.MERCHANT);
        });
        assertEquals(INVALID_DEAL, invalidDealException.getMessage());
    }

    @Test
    void Should_ThrowException_When_WhileGettingDeal() {
        when(dealRepository.findPendingDealById(DEAL_CODE)).thenThrow(new DataAccessException("ERROR") {
        });
        QponCoreException qponCoreException = assertThrows(QponCoreException.class, () -> {
            dealService.deleteDeal(DEAL_CODE, UserType.MERCHANT);
        });
        assertEquals("Read/Write deal from database was failed.", qponCoreException.getMessage());
    }

    private Deal getDeal() {
        DealRequestDto dealRequestDto = getDealRequestDto();
        Set<Brand> brandSet = new HashSet<>();
        Set<Category> categorySet = new HashSet<>();
        brandSet.add(new Brand("bid-001", "new Brand", "description",
                "http://google.com", 1667217882000L, 1672488282000L));
        categorySet.add(new Category("cid-001", "hot category", "description",
                "http://go.lk", CategoryType.NORMAL, null, 1672488282000L,
                1667217882000L, 1667217882000L, true));
        return new Deal(dealRequestDto, brandSet, categorySet, "DLM-2203120015");
    }

    private BasicMerchantBusinessResponseDto getBasicMerchantBusinessResponseDto() {
        BasicMerchantBusinessResponseDto basicMerchantBusinessResponseDto = new BasicMerchantBusinessResponseDto();
        basicMerchantBusinessResponseDto.setId("uid-001");
        basicMerchantBusinessResponseDto.setName("Roy");
        basicMerchantBusinessResponseDto.setImageUrl("goo.le/image");
        basicMerchantBusinessResponseDto.setApprovalStatus(ApprovalStatus.APPROVED);
        basicMerchantBusinessResponseDto.setActive(true);
        return basicMerchantBusinessResponseDto;
    }

    private BusinessMerchantResponseDto getSampleBusinessMerchantResponseDto() {
        BusinessMerchantResponseDto businessMerchantResponseDto = new BusinessMerchantResponseDto();
        businessMerchantResponseDto.setBusinessId("uid-001");
        businessMerchantResponseDto.setBusinessName("Roy");
        businessMerchantResponseDto.setImageUrl("goo.le/image");
        businessMerchantResponseDto.setOwnerName("Roy Owner");
        businessMerchantResponseDto.setAddress("Colombo");
        businessMerchantResponseDto.setWebSite("goo.le/image2");
        businessMerchantResponseDto.setApprovalStatus(ApprovalStatus.APPROVED);
        businessMerchantResponseDto.setActive(true);
        return businessMerchantResponseDto;
    }

    private DealRequestDto getDealRequestDto() {
        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("https://github.com/1.png");
        imgUrls.add("https://github.com/2.png");
        imgUrls.add("https://github.com/3.png");

        List<String> brandList = new ArrayList<>();
        brandList.add("bid-2225");

        List<String> categoryList = new ArrayList<>();
        categoryList.add("cid-266565");

        return new DealRequestDto(
                "New Year Deal!", "for new year",
                "The Description", "No Terms And Conditions",
                imgUrls, "https://github.com/cover.png",
                12.25, 50,
                DeductionType.AMOUNT, 2.22, 5.00,
                1667217882000L, 1672488282000L,
                "uid-0022", brandList, categoryList, "uid-2222");
    }
}