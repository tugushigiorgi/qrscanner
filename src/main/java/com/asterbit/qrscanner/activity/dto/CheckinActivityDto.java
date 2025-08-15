package com.asterbit.qrscanner.activity.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class CheckinActivityDto {

    private boolean checkedIn;
    private LocalDateTime checkinDate;
    private LocalDateTime  activityStart;
    private LocalDateTime  activityEnd;
    private String  ClassroomLocation;
    private String ClassroomName;

}
