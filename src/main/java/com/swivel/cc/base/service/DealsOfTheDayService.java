package com.swivel.cc.base.service;

import com.swivel.cc.base.domain.entity.*;
import com.swivel.cc.base.domain.request.BulkUserRequestDto;
import com.swivel.cc.base.domain.response.DealsOfTheDayHistoryResponseDto;
import com.swivel.cc.base.domain.response.MerchantBusinessResponseDto;
import com.swivel.cc.base.enums.DealsOfTheDayHistoryFilter;
import com.swivel.cc.base.enums.UserType;
import com.swivel.cc.base.exception.InvalidDealException;
import com.swivel.cc.base.exception.QponCoreException;
import com.swivel.cc.base.repository.*;
import org.apache.commons.lang.time.DateUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.TemporalAdjusters;
import java.util.*;

/**
 * Deals of the day service
 */
@Service
public class DealsOfTheDayService {

    private static final String ALL = "ALL";
    private static final int WEEK_DAYS = 7;
    private static final int SIX = 6;
    private static final int ZERO = 0;
    private static final int MINUS_DAYS = -1;
    private static final String INVALID_DEAL = "Invalid deal Id: ";
    private static final String FAILED_TO_READ_DEAL = "Read deals of the day from database was failed.";
    private final DealsOfTheDayRepository dealsOfTheDayRepository;
    private final BankDealsOfTheDayRepository bankDealsOfTheDayRepository;
    private final DealRepository dealRepository;
    private final BankDealRepository bankDealRepository;
    private final DealsOfTheDaySearchIndexService dealsOfTheDaySearchIndexService;
    private final DealSearchIndexRepository dealSearchIndexRepository;
    private final AuthUserService authUserService;
    @Value("${dealsOfTheDay.limit}")
    private int dealsOfTheDayLimit;


    @Autowired
    public DealsOfTheDayService(DealsOfTheDayRepository dealsOfTheDayRepository,
                                BankDealsOfTheDayRepository bankDealsOfTheDayRepository, DealRepository dealRepository,
                                BankDealRepository bankDealRepository, DealsOfTheDaySearchIndexService dealsOfTheDaySearchIndexService,
                                DealSearchIndexRepository dealSearchIndexRepository, AuthUserService authUserService) {
        this.dealsOfTheDayRepository = dealsOfTheDayRepository;
        this.bankDealsOfTheDayRepository = bankDealsOfTheDayRepository;
        this.dealRepository = dealRepository;
        this.bankDealRepository = bankDealRepository;
        this.dealsOfTheDaySearchIndexService = dealsOfTheDaySearchIndexService;
        this.dealSearchIndexRepository = dealSearchIndexRepository;
        this.authUserService = authUserService;
    }

    /**
     * This method execute at 00:00 am to add randomly selected active 10 deals and update the existing
     * active deals of the days to false
     * <p>
     * 24/7 -> cron = "0 0 0 * * *"
     * per 20 -> seconds = fixedRate = 20000
     */
    @Scheduled(cron = "0 0 0 * * *", zone = "GMT +05:30")
    public void saveDealsOfTheDay() {
        try {
            dealsOfTheDayRepository.disableActiveDeals();
            dealsOfTheDaySearchIndexService.deleteAll();
            List<DealSearchIndex> dealSearchIndexList =
                    dealSearchIndexRepository.getRandomDealsForMerchant(System.currentTimeMillis(), dealsOfTheDayLimit);
            List<DealsOfTheDay> dealsOfTheDayList = createDealsOfTheDayList(dealSearchIndexList);
            List<DealOfTheDaySearchIndex> dealOfTheDaySearchIndexList =
                    createDealsOfTheDaySearchIndexList(dealSearchIndexList);
            dealsOfTheDayRepository.saveAll(dealsOfTheDayList);
            dealsOfTheDaySearchIndexService.saveAll(dealOfTheDaySearchIndexList);
            saveBankDealsOfTheDay();
        } catch (DataAccessException e) {
            throw new QponCoreException(FAILED_TO_READ_DEAL, e);
        }
    }

    /**
     * Save randomly selected active 10 bank deals and update the existing active bank deals of the days to false.
     */
    public void saveBankDealsOfTheDay() {
        try {
            bankDealsOfTheDayRepository.disableActiveDeals();
            List<DealSearchIndex> dealSearchIndexList =
                    dealSearchIndexRepository.getRandomDealsForBank(System.currentTimeMillis(), dealsOfTheDayLimit);
            List<BankDealsOfTheDay> dealsOfTheDayListForBank = createDealsOfTheDayListForBank(dealSearchIndexList);
            List<DealOfTheDaySearchIndex> dealOfTheDaySearchIndexList =
                    createDealsOfTheDaySearchIndexList(dealSearchIndexList);
            bankDealsOfTheDayRepository.saveAll(dealsOfTheDayListForBank);
            dealsOfTheDaySearchIndexService.saveAll(dealOfTheDaySearchIndexList);
        } catch (DataAccessException e) {
            throw new QponCoreException(FAILED_TO_READ_DEAL, e);
        }
    }

    /**
     * This method get deals of the day page and convert it into deals page
     *
     * @return deal page
     */
    public Page<Deal> dealsOfTheDayPageForMerchant(Pageable pageable, String categoryId,
                                                   String merchantId, String searchTerm, UserType dealSource) {
        try {
            var dealOfTheDaySearchIndexPage =
                    dealsOfTheDaySearchIndexService
                            .getAllSearchDeals(pageable, categoryId, merchantId, searchTerm, dealSource);
            List<Deal> dealList = new ArrayList<>();
            dealOfTheDaySearchIndexPage.forEach(dealOfTheDaySearchIndex -> {

                if (UserType.MERCHANT.equals(dealSource)) {
                    var optionalDealFromDb = dealRepository.findById(dealOfTheDaySearchIndex.getId());
                    if (optionalDealFromDb.isPresent()) {
                        dealList.add(optionalDealFromDb.get());
                    } else {
                        throw new InvalidDealException(INVALID_DEAL + dealOfTheDaySearchIndex.getId());
                    }
                } else {
                    Optional<BankDeal> optionalBankDealFromDb = bankDealRepository.findById(dealOfTheDaySearchIndex.getId());
                    if (optionalBankDealFromDb.isPresent()) {
                        dealList.add(new Deal(optionalBankDealFromDb.get()));
                    } else {
                        throw new InvalidDealException(INVALID_DEAL + dealOfTheDaySearchIndex.getId());
                    }
                }
            });
            return new PageImpl<>(dealList);
        } catch (DataAccessException e) {
            throw new QponCoreException(FAILED_TO_READ_DEAL, e);
        }
    }

    /**
     * This method create deals of the day list from deals list
     *
     * @param dealSearchIndexList dealSearchIndexList
     * @return deals of the day list
     */
    private List<DealsOfTheDay> createDealsOfTheDayList(List<DealSearchIndex> dealSearchIndexList) {
        var dealsOfTheDays = new ArrayList<DealsOfTheDay>();
        for (DealSearchIndex dealSearchIndex : dealSearchIndexList) {
            Optional<Deal> deal = dealRepository.findById(dealSearchIndex.getId());
            deal.ifPresent(value -> dealsOfTheDays.add(new DealsOfTheDay(value)));
        }
        return dealsOfTheDays;
    }

    /**
     * This method create deals of the day list from bank deals list.
     *
     * @param dealSearchIndexList dealSearchIndexList
     * @return bank deals of the day list.
     */
    private List<BankDealsOfTheDay> createDealsOfTheDayListForBank(List<DealSearchIndex> dealSearchIndexList) {
        var dealsOfTheDays = new ArrayList<BankDealsOfTheDay>();
        for (DealSearchIndex dealSearchIndex : dealSearchIndexList) {
            Optional<BankDeal> bankDeal = bankDealRepository.findById(dealSearchIndex.getId());
            bankDeal.ifPresent(value -> dealsOfTheDays.add(new BankDealsOfTheDay(value)));
        }
        return dealsOfTheDays;
    }

    /**
     * This method create deals of the day search index from deals list
     *
     * @param dealSearchIndexList dealSearchIndexList
     * @return DealOfTheDaySearchIndex list
     */
    private List<DealOfTheDaySearchIndex> createDealsOfTheDaySearchIndexList(List<DealSearchIndex> dealSearchIndexList) {
        var dealOfTheDaySearchIndexArrayList = new ArrayList<DealOfTheDaySearchIndex>();
        dealSearchIndexList.forEach(dealSearchIndex ->
                dealOfTheDaySearchIndexArrayList.add(new DealOfTheDaySearchIndex(dealSearchIndex)));
        return dealOfTheDaySearchIndexArrayList;
    }

    /**
     * This method is used to get deals of the day history.
     *
     * @param timeZone   timeZone
     * @param searchTerm searchTerm
     * @param filter     filter
     * @param userId     userId
     * @return deals of the day list.
     */
    public List<DealsOfTheDayHistoryResponseDto> getDealsOfTheDayHistory(String timeZone, String searchTerm,
                                                                         DealsOfTheDayHistoryFilter filter,
                                                                         String userId, UserType userType, int page) {

        LocalDate thisWeekMonday =
                LocalDate.now(ZoneId.of(timeZone)).with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        Date today = new Date();
        Date yesterday = DateUtils.addDays(today, MINUS_DAYS);
        List<DealsOfTheDayHistoryResponseDto> historyResponseDto = new ArrayList<>();
        try {
            switch (filter) {
                case TODAY:
                    return createDealsOfTheDayHistoryResponse(timeZone, searchTerm, userId, today, userType);
                case YESTERDAY:
                    return createDealsOfTheDayHistoryResponse(timeZone, searchTerm, userId, yesterday, userType);
                case LAST_WEEK:
                    return createDealsOfTheDayLastWeekHistoryResponse(timeZone, searchTerm, userId, thisWeekMonday,
                            userType, page);
            }
            return historyResponseDto;
        } catch (DataAccessException e) {
            throw new QponCoreException("Failed to get deals of the day history from db", e);
        }
    }

    /**
     * This method is used to get deals-of-the-day by date.
     *
     * @param timeZone   timeZone
     * @param searchTerm searchTerm
     * @param userId     userId
     * @param date       date
     * @return deals-of-the-day by date.
     */
    private List<DealsOfTheDayHistoryResponseDto> createDealsOfTheDayHistoryResponse(String timeZone, String searchTerm,
                                                                                     String userId, Date date,
                                                                                     UserType userType) {

        List<DealsOfTheDayHistoryResponseDto> responseDto = new ArrayList<>();
        List<Deal> dealList = getDealList(searchTerm, userType, date);
        Map<String, MerchantBusinessResponseDto> merchantMap = getMerchantFromAuth(dealList, userId, userType);
        responseDto.add(new DealsOfTheDayHistoryResponseDto(date.getTime(), timeZone, dealList, merchantMap));
        return responseDto;
    }

    /**
     * This method is used to get last week's deals-of-the-day.
     *
     * @param timeZone       timeZone
     * @param searchTerm     searchTerm
     * @param userId         userId
     * @param thisWeekMonday monday this week.
     * @return last week's deals-of-the-day.
     */
    private List<DealsOfTheDayHistoryResponseDto> createDealsOfTheDayLastWeekHistoryResponse(String timeZone,
                                                                                             String searchTerm,
                                                                                             String userId,
                                                                                             LocalDate thisWeekMonday,
                                                                                             UserType userType,
                                                                                             int page) {
        LocalDate lastWeekMonday = thisWeekMonday.minusDays(WEEK_DAYS);
        List<Deal> dealList = new ArrayList<>();
        List<DealsOfTheDayHistoryResponseDto> responseDto = new ArrayList<>();
        Map<String, MerchantBusinessResponseDto> merchantMap;
        Date lastWeekDate;

        for (int day = SIX; day >= ZERO; day--) {
            lastWeekDate = Date.from(lastWeekMonday.plusDays(day).atStartOfDay(ZoneId.of(timeZone)).toInstant());
            if (day == (SIX - page)) {
                dealList = getDealList(searchTerm, userType, lastWeekDate);
            }
            merchantMap = getMerchantFromAuth(dealList, userId, userType);
            responseDto.add(new DealsOfTheDayHistoryResponseDto(lastWeekDate.getTime(), timeZone,
                    dealList, merchantMap));
        }
        return responseDto;
    }

    /**
     * This method is used to get deals list from deals of the day database.
     *
     * @param searchTerm searchTerm
     * @param userType   merchant/bank
     * @param date       date
     * @return list of deals.
     */
    private List<Deal> getDealList(String searchTerm, UserType userType, Date date) {
        if (searchTerm.equals(ALL)) {
            return userType.equals(UserType.MERCHANT) ?
                    dealsOfTheDayRepository.getDealByAppliedOn(date) :
                    convertBankDealListToDealList(bankDealsOfTheDayRepository.getDealByAppliedOn(date));
        } else {
            return userType.equals(UserType.MERCHANT) ?
                    dealsOfTheDayRepository.getDealsOfTheDayByDateAndSearchTerm(searchTerm, date) :
                    convertBankDealListToDealList(bankDealsOfTheDayRepository
                            .getDealsOfTheDayByDateAndSearchTerm(searchTerm, date));
        }
    }

    /**
     * This method is used to convert bank deal list into deal list.
     *
     * @param bankDealList bankDealList
     * @return deals list.
     */
    private List<Deal> convertBankDealListToDealList(List<BankDeal> bankDealList) {
        List<Deal> dealList = new ArrayList<>();
        for (BankDeal bankDeal : bankDealList) {
            dealList.add(new Deal(bankDeal));
        }
        return dealList;
    }

    /**
     * Used to get merchant details from auth.
     *
     * @param dealList dealList
     * @param userId   userId
     * @return merchant map.
     */
    private Map<String, MerchantBusinessResponseDto> getMerchantFromAuth(List<Deal> dealList, String userId,
                                                                         UserType userType) {
        List<String> merchantIds = new ArrayList<>();
        if (dealList.isEmpty())
            return new HashMap<>();
        for (Deal deal : dealList) {
            merchantIds.add(deal.getMerchantId());
        }
        return authUserService.getMerchantMap(userId, new BulkUserRequestDto(merchantIds), userType);
    }
}
