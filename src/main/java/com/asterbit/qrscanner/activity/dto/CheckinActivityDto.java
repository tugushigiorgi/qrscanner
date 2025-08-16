package com.asterbit.qrscanner.activity.dto;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CheckinActivityDto {
  private LocalDateTime checkinDate;
  private LocalDateTime activityStart;
  private LocalDateTime activityEnd;
  private String ClassroomLocation;
  private String ClassroomName;
}
