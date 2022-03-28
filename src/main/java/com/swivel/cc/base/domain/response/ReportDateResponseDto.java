package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.enums.ReportDateOption;
import com.swivel.cc.base.exception.QponCoreException;
import lombok.Getter;
import lombok.Setter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.ZoneId;
import java.util.Date;

/**
 * Report date response dto
 * Used for Google Analytics date formats.
 * Formats are:
 * Daily: 20210101 (2021 January 1)
 * Weekly: 202110 (2021 week 10)
 * Monthly: 2021 01 (2021 January)
 * Yearly: 2021
 */
@Getter
@Setter
public class ReportDateResponseDto extends ResponseDto {

    private static final String SPACE = " ";
    private static final int CALENDER_MONTHS = 12;
    private static final int WEEKS_IN_A_MONTH = 6;
    private String displayDate;
    private int startYear;
    private int endYear;

    public ReportDateResponseDto(ReportDateOption dateOption, String dateInString, long startDate, long endDate,
                                 String timeZone) {
        try {
            switch (dateOption) {
                case DAILY:
                    var dailyDate = new SimpleDateFormat(ReportDateOption.DAILY.getDateFromFormat())
                            .parse(dateInString);
                    var dailyDateFormat = new SimpleDateFormat(ReportDateOption.DAILY.getDateToFormat());
                    this.displayDate = dailyDateFormat.format(dailyDate);
                    break;
                case WEEKLY:
                    setStartAndEndYear(startDate, endDate, timeZone);
                    var weeklyDate = new SimpleDateFormat(ReportDateOption.WEEKLY.getDateFromFormat())
                            .parse(combineWeekAndYear(dateInString));
                    var weeklyDateFormat = new SimpleDateFormat(ReportDateOption.WEEKLY.getDateToFormat());
                    this.displayDate = addSuffixForWeek(weeklyDateFormat.format(weeklyDate));
                    break;
                case MONTHLY:
                    setStartAndEndYear(startDate, endDate, timeZone);
                    var monthlyDate = new SimpleDateFormat(ReportDateOption.MONTHLY.getDateFromFormat())
                            .parse(combineMonthAndYear(dateInString, startDate, timeZone));
                    var monthlyDateFormat = new SimpleDateFormat(ReportDateOption.MONTHLY.getDateToFormat());
                    this.displayDate = monthlyDateFormat.format(monthlyDate);
                    break;
                case YEARLY:
                    var yearlyDate = new SimpleDateFormat(ReportDateOption.YEARLY.getDateFromFormat())
                            .parse(dateInString);
                    var yearlyDateFormat = new SimpleDateFormat(ReportDateOption.YEARLY.getDateToFormat());
                    this.displayDate = yearlyDateFormat.format(yearlyDate);
                    break;
                default:
                    this.displayDate = "";
            }
        } catch (ParseException e) {
            throw new QponCoreException("Converting to display date text failed", e);
        }
    }

    /**
     * This method is used to set start & end years.
     *
     * @param startDate startDate
     * @param endDate   endDate
     * @param timeZone  timeZone
     */
    private void setStartAndEndYear(long startDate, long endDate, String timeZone) {
        Date start = new Date(startDate);
        this.startYear = start.toInstant().atZone(ZoneId.of(timeZone)).toLocalDate().getYear();
        Date end = new Date(endDate);
        this.endYear = end.toInstant().atZone(ZoneId.of(timeZone)).toLocalDate().getYear();
    }

    /**
     * This method is used to combine analytics week response with correct year.
     *
     * @param week week
     * @return combined week & year.
     */
    private String combineWeekAndYear(String week) {
        if (startYear < endYear && Integer.parseInt(week) < WEEKS_IN_A_MONTH) {
            return endYear + week;
        }
        return startYear + week;
    }

    /**
     * This method is used to combine analytics month response with correct year.
     *
     * @param month     month
     * @param startDate startDate
     * @param timeZone  timeZone
     * @return combined month & year.
     */
    private String combineMonthAndYear(String month, long startDate, String timeZone) {
        Date start = new Date(startDate);
        int startMonth = start.toInstant().atZone(ZoneId.of(timeZone)).toLocalDate().getMonthValue();
        int monthIndexFromStartMonth = Integer.parseInt(month) + startMonth;
        if (monthIndexFromStartMonth > CALENDER_MONTHS) {
            return endYear + SPACE + (monthIndexFromStartMonth - CALENDER_MONTHS);
        }
        return startYear + SPACE + (monthIndexFromStartMonth);
    }

    /**
     * This method is used to add suffix for week display text.
     *
     * @param weekString weekString
     * @return week with suffix
     */
    private String addSuffixForWeek(String weekString) {
        String weekWithSuffix;
        var monthAndYear = weekString.substring(3);
        var weekIndex = weekString.charAt(1);

        switch (weekIndex) {
            case '1':
                weekWithSuffix = weekIndex + "st";
                break;
            case '2':
                weekWithSuffix = weekIndex + "nd";
                break;
            case '3':
                weekWithSuffix = weekIndex + "rd";
                break;
            case '4':
            case '5':
            case '6':
                weekWithSuffix = weekIndex + "th";
                break;
            default:
                weekWithSuffix = "";
        }
        return weekWithSuffix + " week - " + monthAndYear;
    }

    @Override
    public String toLogJson() {
        return toJson();
    }

}
