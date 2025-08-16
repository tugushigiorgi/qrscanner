package com.asterbit.qrscanner.classroom.dto;

import com.asterbit.qrscanner.activity.dto.ActivityDto;
import java.util.Set;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentActivitiesDto {
  private String checkInToken;
  private Set<ActivityDto> activities;
}
