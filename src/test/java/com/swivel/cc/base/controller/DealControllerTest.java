package com.swivel.cc.base.controller;

import com.swivel.cc.base.configuration.Translator;
import com.swivel.cc.base.domain.request.DealRequestDto;
import com.swivel.cc.base.domain.response.BusinessMerchantResponseDto;
import com.swivel.cc.base.enums.ApprovalStatus;
import com.swivel.cc.base.enums.DeductionType;
import com.swivel.cc.base.exception.InvalidBrandException;
import com.swivel.cc.base.exception.InvalidCategoryException;
import com.swivel.cc.base.exception.InvalidUserException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.startsWith;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * This class tests the {@link DealController} class.
 */
class DealControllerTest {
    private static final String CREATE_DEAL_URI = "/api/v1/deals";
    private static final String TIME_ZONE = "Asia/Colombo";
    private static final String IN_VALID_TIME_ZONE = "Japan/Tokio";
    private static final String ACCESS_TOKEN = "Bearer192f0c96-dd19-404e-8217-a8fd1cc81d55";
    private static final String MERCHANT_ID = "uid-001";
    private static final long INVALID_TIME_STAMP = 1234556;

    @Mock
    private DealService dealService;
    @Mock
    private BrandService brandService;
    @Mock
    private CategoryService categoryService;
    @Mock
    private AuthUserService authUserService;
    @Mock
    private Translator translator;
    @Mock
    private DealCodeService dealCodeService;
    private MockMvc mockMvc;

    @BeforeEach
    void setUp() {
        initMocks(this);
        DealController dealController = new DealController(dealService, brandService, categoryService,
                authUserService, translator, dealCodeService);
        mockMvc = MockMvcBuilders.standaloneSetup(dealController).build();
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_ReturnOk_When_CreatingADeal() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("SUCCESS")))
                .andExpect(jsonPath("$.message", is("Successfully created the deal.")))
                .andExpect(jsonPath("$.statusCode", is(2005)))
                .andExpect(jsonPath("$.data.id", startsWith("did-")));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingADealWithoutRequiredFields() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        dealRequestDto.setTitle(null);
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.message", is("Required fields are missing.")))
                .andExpect(jsonPath("$.errorCode", is(4000)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingADealWithAInvalidStartDate() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        dealRequestDto.setValidFrom(INVALID_TIME_STAMP);
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID))
                .thenReturn(businessMerchantResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid validFrom date.")))
                .andExpect(jsonPath("$.errorCode", is(4004)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingADealWithAInvalidExpireDate() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        dealRequestDto.setExpiredOn(INVALID_TIME_STAMP);
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID))
                .thenReturn(businessMerchantResponseDto);
        ResultActions error = mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid expiredOn date.")))
                .andExpect(jsonPath("$.errorCode", is(4005)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingADealWithAInvalidOriginalPrice() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        dealRequestDto.setOriginalPrice(-250.00);
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID))
                .thenReturn(businessMerchantResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid price/ amount.")))
                .andExpect(jsonPath("$.errorCode", is(4007)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingADealWithAInvalidTimeZone() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", IN_VALID_TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid time zone.")))
                .andExpect(jsonPath("$.errorCode", is(4100)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingADealWithAInActiveMerchant() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        businessMerchantResponseDto.setActive(false);
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.status", is("ERROR")))
                .andExpect(jsonPath("$.message", is("Invalid/ Inactive merchant cannot create any deals.")))
                .andExpect(jsonPath("$.errorCode", is(4014)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingDealWithInvalidCategoryIds() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);

        when(categoryService.getCategorySetByIds(dealRequestDto.getCategoryIds())).thenThrow(new InvalidCategoryException("ERROR") {
        });
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message", is("Invalid category id.")));

    }

    @Test
    void Should_ReturnBadRequest_When_CreatingDealWithInvalidBrandIds() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        List<String> brandIds = new ArrayList<>();
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);

        when(brandService.getBrandSetByIdList(dealRequestDto.getBrandIds())).thenThrow(new InvalidBrandException("ERROR") {
        });
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message", is("Invalid brand id.")))
                .andExpect(jsonPath("$.errorCode", is(4003)));
    }

    @Test
    void Should_ReturnBadRequest_When_CreatingDealWithInvalidUserDetail() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        List<String> brandIds = new ArrayList<>();
        brandIds.add("bid-01");
        brandIds.add("bid-02");
        dealRequestDto.setBrandIds(brandIds);
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenThrow(new InvalidUserException("ERROR") {
        });
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message", is("Invalid/ Inactive merchant cannot create any deals.")))
                .andExpect(jsonPath("$.errorCode", is(4014)));
    }

    @Test
    void Should_InternalServerError_When_CreatingDeal() throws Exception {
        DealRequestDto dealRequestDto = getDealRequestDto();
        List<String> brandIds = new ArrayList<>();
        brandIds.add("bid-01");
        brandIds.add("bid-02");
        dealRequestDto.setBrandIds(brandIds);
        BusinessMerchantResponseDto businessMerchantResponseDto = getBusinessMerchantResponseDto();
        when(authUserService.getMerchantBusinessByMerchantId(ACCESS_TOKEN, MERCHANT_ID)).thenReturn(businessMerchantResponseDto);
        when(brandService.getBrandSetByIdList(dealRequestDto.getBrandIds())).thenThrow(new QponCoreException("ERROR") {
        });
        mockMvc.perform(MockMvcRequestBuilders.post(CREATE_DEAL_URI)
                        .header("Time-Zone", TIME_ZONE)
                        .header("User-Id", MERCHANT_ID)
                        .header("Auth-Token", ACCESS_TOKEN)
                        .content(dealRequestDto.toJson())
                        .contentType(MediaType.APPLICATION_JSON).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isInternalServerError())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("$.message", is("Internal server error.")))
                .andExpect(jsonPath("$.errorCode", is(5000)));
    }

    private DealRequestDto getDealRequestDto() {
        DealRequestDto dealRequestDto = new DealRequestDto();
        dealRequestDto.setTitle("New Year Deal.");
        dealRequestDto.setSubTitle("New Year 2022");
        dealRequestDto.setDescription("The New Year is near");
        dealRequestDto.setTermsAndConditions("Buy 1500LKR and have the discount");
        List<String> imgUrls = new ArrayList<>();
        imgUrls.add("http://goo.lk/");
        imgUrls.add("http://goo.lk/");
        dealRequestDto.setImageUrls(imgUrls);
        dealRequestDto.setCoverImage("http://goo.lk/");
        dealRequestDto.setOriginalPrice(52.00);
        dealRequestDto.setQuantity(25);
        dealRequestDto.setDeductionType(DeductionType.PERCENTAGE);
        dealRequestDto.setDeductionPercentage(25.25);
        dealRequestDto.setValidFrom(System.currentTimeMillis() + 3800);
        dealRequestDto.setExpiredOn(System.currentTimeMillis() + 900000);
        dealRequestDto.setMerchantId("uid-001");
        List<String> brandId = new ArrayList<>();
        brandId.add("bid-001");
        dealRequestDto.setBrandIds(brandId);
        List<String> categoryId = new ArrayList<>();
        categoryId.add("cid-001");
        dealRequestDto.setCategoryIds(categoryId);
        return dealRequestDto;
    }

    private BusinessMerchantResponseDto getBusinessMerchantResponseDto() {
        BusinessMerchantResponseDto businessMerchantResponseDto = new BusinessMerchantResponseDto();
        businessMerchantResponseDto.setBusinessId("001");
        businessMerchantResponseDto.setMerchantId("M-001");
        businessMerchantResponseDto.setBusinessName("Swivel");
        businessMerchantResponseDto.setOwnerName("Swivel");
        businessMerchantResponseDto.setApprovalStatus(ApprovalStatus.APPROVED);
        businessMerchantResponseDto.setAddress("Dehiwala");
        businessMerchantResponseDto.setEmail("leel@gmail.com");
        businessMerchantResponseDto.setActive(true);
        return businessMerchantResponseDto;
    }
}