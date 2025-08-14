package com.asterbit.qrscanner.classroom.dto;

import com.asterbit.qrscanner.activity.dto.ActivityDto;
import lombok.*;

import java.util.Set;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CurrentActivitiesDto {
    private UUID checkInToken;
    private Set<ActivityDto> activities;
}
