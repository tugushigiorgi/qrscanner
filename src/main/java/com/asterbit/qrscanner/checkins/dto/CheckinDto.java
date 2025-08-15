package com.asterbit.qrscanner.checkins.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class CheckinDto {

    private boolean checkedIn;
    private LocalDateTime checkinDate;
    private LocalDateTime  activityStart;
    private LocalDateTime  activityEnd;
    private String  ClassroomLocation;
    private String ClassroomName;



}
