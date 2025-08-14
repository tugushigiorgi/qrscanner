package com.asterbit.qrscanner.activity.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDto {
    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;
}
