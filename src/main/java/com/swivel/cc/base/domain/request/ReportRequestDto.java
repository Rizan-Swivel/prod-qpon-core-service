package com.swivel.cc.base.domain.request;

import com.swivel.cc.base.enums.ReportDateOption;
import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.TimeUnit;

/**
 * Report request dto
 */
@Setter
@Getter
public class ReportRequestDto extends GroupedReportRequestDto {

    private ReportDateOption option;

    public boolean isRequiredAvailableForReport() {
        return option != null && isRequiredAvailable();
    }

    /**
     * This method is used to validate date option with selected date range.
     *
     * @return true/false
     */
    public boolean isSupportedDateRange() {
        long dateDifference = TimeUnit.DAYS.convert(endDate - startDate, TimeUnit.MILLISECONDS);
        return ((dateDifference < 30 && (option == ReportDateOption.DAILY || option == ReportDateOption.WEEKLY)) ||
                (dateDifference >= 30 && dateDifference < 366 && option == ReportDateOption.MONTHLY) ||
                (dateDifference >= 30 && option == ReportDateOption.YEARLY));
    }
}
