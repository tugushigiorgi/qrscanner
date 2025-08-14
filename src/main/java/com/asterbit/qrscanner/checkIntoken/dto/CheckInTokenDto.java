package com.asterbit.qrscanner.checkIntoken.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CheckInTokenDto {
    private UUID token;
}
