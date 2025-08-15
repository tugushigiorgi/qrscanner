package com.asterbit.qrscanner.classroom.dto;

import com.asterbit.qrscanner.activity.dto.ActivityDto;
import lombok.*;

import java.util.Set;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentActivitiesDto {
    private String checkInToken;
    private Set<ActivityDto> activities;
}
