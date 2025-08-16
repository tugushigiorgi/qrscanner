package com.asterbit.qrscanner.classroom.dto;


import jakarta.validation.constraints.NotNull;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class CheckinStudentDto {

  @NotNull
  public String token;
  @NotNull
  public UUID activityId;

}
