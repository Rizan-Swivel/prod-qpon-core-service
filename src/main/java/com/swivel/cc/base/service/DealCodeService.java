package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.DealCode;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.DealCodeRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Optional;

/**
 * Deal code service
 */
@Service
public class DealCodeService {

    private static final int YEAR_START_POSITION = 2;
    private static final int YEAR_END_POSITION = 4;
    private static final int MONTH_START_POSITION = 5;
    private static final int MONTH_END_POSITION = 7;
    private static final int DAY_START_POSITION = 8;
    private static final int DAY_END_POSITION = 10;
    private static final String BANK_DEAL_PREFIX = "B";
    private static final String MERCHANT_DEAL_PREFIX = "M";
    private static final int DEFAULT_DEAL_NUMBER_FOR_THE_DAY = 0;
    private static final String DEAL_CODE_VALUE_FORMATTER = "%04d";
    private static final String HASH = "#";
    private static final String YY = "YY";
    private static final String MM = "MM";
    private static final String DD = "DD";
    private static final String DOLLAR_SUFFIX = "$$$$";

    private final DealCodeRepository dealCodeRepository;
    String dealCodeFormat;

    public DealCodeService(@Value("${dealCode.dealCodeFormat}") String dealCodeFormat,
                           DealCodeRepository dealCodeRepository) {
        this.dealCodeRepository = dealCodeRepository;
        this.dealCodeFormat = dealCodeFormat;
    }

    /**
     * This method used to generate and save the next deal code.
     *
     * @param localCurrentDate current local date
     * @param dealType         deal type
     * @return deal code
     */
    public String generateAndSave(LocalDate localCurrentDate, UserType dealType) {
        Optional<DealCode> lastDealDetail = getLastDealDetail(localCurrentDate);
        int latestDealNumberForTheDay = getLatestDealNumberForTheDay(lastDealDetail, localCurrentDate);
        String generatedNextDealCode = generateNext(localCurrentDate, dealType, latestDealNumberForTheDay);
        saveDealCode(generatedNextDealCode, latestDealNumberForTheDay + 1, dealType);
        return generatedNextDealCode;
    }

    /**
     * This method used to generate next deal code values and pass them to formatNextDealCode method.
     *
     * @param localCurrentDate current date
     * @param dealType         deal type
     * @param dealNumber       deal number
     * @return deal code
     */
    private String generateNext(LocalDate localCurrentDate, UserType dealType, int dealNumber) {
        var year = localCurrentDate.toString().substring(YEAR_START_POSITION, YEAR_END_POSITION);
        var month = localCurrentDate.toString().substring(MONTH_START_POSITION, MONTH_END_POSITION);
        var day = localCurrentDate.toString().substring(DAY_START_POSITION, DAY_END_POSITION);
        var dealTypeLetter = (dealType == UserType.MERCHANT) ? MERCHANT_DEAL_PREFIX : BANK_DEAL_PREFIX;
        return formatNextDealCode(year, month, day, dealTypeLetter, dealNumber);
    }

    /**
     * This method used to format next deal code.
     *
     * @param year           year
     * @param month          month
     * @param day            day
     * @param dealTypeLetter deal type letter
     * @param dealNumber     deal number
     * @return formatted next deal code
     */
    private String formatNextDealCode(String year, String month, String day, String dealTypeLetter, int dealNumber) {
        var newDealCodeNumber = String.format(DEAL_CODE_VALUE_FORMATTER, dealNumber + 1);
        return dealCodeFormat
                .replace(HASH, dealTypeLetter)
                .replace(YY, year)
                .replace(MM, month)
                .replace(DD, day)
                .replace(DOLLAR_SUFFIX, newDealCodeNumber);
    }

    /**
     * This method is used to get the last deal code.
     *
     * @param localCurrentDate current local date
     * @return optional deal code
     */
    private Optional<DealCode> getLastDealDetail(LocalDate localCurrentDate) {
        try {
            return dealCodeRepository.findTopByDealDateOrderByDealDateDescCreatedAtDesc(localCurrentDate);
        } catch (QponCoreException e) {
            throw new QponCoreException("Retrieving  last deal code was failed.", e);
        }
    }

    /**
     * This method returns the latest deal number for the day.
     *
     * @param dealCodeOptional Optional last deal code
     * @param localCurrentDate current date
     * @return new deal code.
     */
    private int getLatestDealNumberForTheDay(Optional<DealCode> dealCodeOptional, LocalDate localCurrentDate) {
        if (dealCodeOptional.isPresent()) {
            if (dealCodeOptional.get().getDealDate().equals(localCurrentDate)) {
                return dealCodeOptional.get().getDealNumberForTheDay();
            } else {
                return DEFAULT_DEAL_NUMBER_FOR_THE_DAY;
            }
        } else {
            return DEFAULT_DEAL_NUMBER_FOR_THE_DAY;
        }
    }

    /**
     * This method saves the current deal code.
     *
     * @param dealCode            deal code
     * @param dealNumberForTheDay latest deal number
     * @param dealType            deal type
     */
    private void saveDealCode(String dealCode, int dealNumberForTheDay, UserType dealType) {
        try {
            var newDealCode = new DealCode(dealCode,
                    LocalDate.now(), dealNumberForTheDay, dealType, System.currentTimeMillis());
            dealCodeRepository.save(newDealCode);
        } catch (QponCoreException e) {
            throw new QponCoreException("Saving deal code: " + dealCode + " into database was failed", e);
        }
    }
}