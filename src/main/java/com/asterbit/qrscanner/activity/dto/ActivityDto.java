package com.asterbit.qrscanner.activity.dto;

import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ActivityDto {

    private UUID id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;
}
