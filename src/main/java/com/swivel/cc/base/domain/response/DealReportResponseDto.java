package com.swivel.cc.base.domain.response;

import com.swivel.cc.base.domain.entity.Deal;
import com.swivel.cc.base.enums.DealActiveStatusType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class DealReportResponseDto extends ResponseDto {

    private String id;
    private String title;
    private String coverImage;
    private DealActiveStatusType activeStatus;

    public DealReportResponseDto(Deal deal) {
        this.id = deal.getId();
        this.title = deal.getTitle();
        this.coverImage = deal.getCoverImage();
        this.activeStatus = deal.getExpiredOn() > System.currentTimeMillis() ?
                DealActiveStatusType.ACTIVE : DealActiveStatusType.INACTIVE;
    }
}
