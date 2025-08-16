package com.asterbit.qrscanner.activity.dto;

import java.time.LocalDateTime;
import java.util.UUID;
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
public class ActivityDto {

  private UUID id;

  private String title;

  private LocalDateTime startTime;

  private LocalDateTime endTime;

  private String description;
}
