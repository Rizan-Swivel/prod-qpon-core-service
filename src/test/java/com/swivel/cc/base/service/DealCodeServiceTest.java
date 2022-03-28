package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.DealCode;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.DealCodeRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

class DealCodeServiceTest {

    private static final String DEAL_CODE_FORMAT = "DL#-YYMMDD$$$$";
    private static final long CREATED_AT = 14452154;
    private DealCodeService dealCodeService;
    @Mock
    private DealCodeRepository dealCodeRepository;

    @BeforeEach
    void setUp() {
        initMocks(this);
        dealCodeService = new DealCodeService(DEAL_CODE_FORMAT, dealCodeRepository);
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void Should_Return_DLM2203210005_When_LocalDate2022_03_21AndNextDealNumberIs4AndUserTypeIsMerchant() {
        var date = getSampleLocalDate();
        var dealCode =
                new DealCode("DLM-2203220004", date, 4, UserType.MERCHANT, CREATED_AT);
        Optional<DealCode> dealCodeOptional = Optional.of(dealCode);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        String generatedDealCode = dealCodeService.generateAndSave(date, UserType.MERCHANT);
        assertEquals("DLM-2203220005", generatedDealCode);
    }

    @Test
    void Should_Return_DLM2203220004_When_LocalDate2022_03_22AndNextDealNumberIs3AndUserTypeIsMerchant() {
        var date = getSampleLocalDate();
        var dealCode =
                new DealCode("DLM-2203220003", date, 3, UserType.MERCHANT, CREATED_AT);
        Optional<DealCode> dealCodeOptional = Optional.of(dealCode);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        String generatedDealCode = dealCodeService.generateAndSave(date, UserType.MERCHANT);
        assertEquals("DLM-2203220004", generatedDealCode);
    }

    @Test
    void Should_Return_DLM2203200001_When_LocalDate2022_03_20AndPreviousLastDealDateIs2022_03_22() {
        var date = getSampleLocalDate();
        var dealCode =
                new DealCode("DLM-2203200003", date, 3, UserType.MERCHANT, CREATED_AT);
        Optional<DealCode> dealCodeOptional = Optional.of(dealCode);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        String generatedDealCode = dealCodeService.generateAndSave(date.plusDays(1), UserType.MERCHANT);
        assertEquals("DLM-2203230001", generatedDealCode);
    }

    @Test
    void Should_Return_DLB2203210001_When_LocalDate2022_03_21AndNoDealsForTheDayAndUserTypeIsBank() {
        var date = getSampleLocalDate();
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(Optional.empty());
        String generatedDealCode = dealCodeService.generateAndSave(date, UserType.BANK);
        assertEquals("DLB-2203220001", generatedDealCode);
    }

    @Test
    void Should_Return_DLM2203222125_When_LocalDate2022_03_22AndCurrentDealNumberIs2124UserTypeIsMerchant() {
        var date = getSampleLocalDate();
        var dealCode =
                new DealCode("DLM-2203200024", date, 2124, UserType.MERCHANT, CREATED_AT);
        Optional<DealCode> dealCodeOptional = Optional.of(dealCode);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        String generatedDealCode = dealCodeService.generateAndSave(date, UserType.MERCHANT);
        assertEquals("DLM-2203222125", generatedDealCode);
    }

    @Test
    void Should_Return_DLM2203225841_When_LocalDate2022_03_21AndCurrentDealNumberIs5840AndUserTypeIsMerchant() {
        var date = getSampleLocalDate();
        var dealCode =
                new DealCode("DLM-2203225840", date, 5840, UserType.MERCHANT, CREATED_AT);
        Optional<DealCode> dealCodeOptional = Optional.of(dealCode);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        String generatedDealCode = dealCodeService.generateAndSave(date, UserType.MERCHANT);
        assertEquals("DLM-2203225841", generatedDealCode);
    }

    @Test
    void Should_Return_DLM2203230001_When_LocalDate2022_03_23AndNoDealsAndUserTypeIsBank() {
        var date = getSampleLocalDate();
        Optional<DealCode> dealCodeOptional = Optional.ofNullable(null);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        String generatedDealCode = dealCodeService.generateAndSave(date, UserType.BANK);
        assertEquals("DLB-2203220001", generatedDealCode);
    }

    @Test
    void Should_ThrowQponCoreException_When_GettingLastDealCode() {
        var date = getSampleLocalDate();
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date))
                .thenThrow(new QponCoreException("Generating deal code was failed"));
        QponCoreException qponCoreException = assertThrows(QponCoreException.class, () -> {
            dealCodeService.generateAndSave(date, UserType.MERCHANT);
        });
        assertEquals("Retrieving  last deal code was failed.", qponCoreException.getMessage());
    }

    @Test
    void Should_ThrowQponCoreException_When_SavingDealCode() {
        var date = getSampleLocalDate();
        when(dealCodeRepository.save(any())).thenThrow(new QponCoreException("Saving deal code:"));
        QponCoreException qponCoreException = assertThrows(QponCoreException.class, () -> {
            dealCodeService.generateAndSave(date, UserType.MERCHANT);
        });
        assertEquals("Saving deal code: DLM-2203220001 into database was failed", qponCoreException.getMessage());
    }

    @Test
    void Should_SaveDealCodeOneTimeWhenGenerateDealCode() {
        var date = getSampleLocalDate();
        Optional<DealCode> dealCodeOptional = Optional.ofNullable(null);
        when(dealCodeRepository
                .findTopByDealDateOrderByDealDateDescCreatedAtDesc(date)).thenReturn(dealCodeOptional);
        dealCodeService.generateAndSave(date, UserType.MERCHANT);
        verify(dealCodeRepository, times(1)).save(any());
    }

    private LocalDate getSampleLocalDate() {
        return LocalDate.of(Integer.parseInt("2022"), Integer.parseInt("03"), Integer.parseInt("22"));
    }
}

