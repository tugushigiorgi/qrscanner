package com.asterbit.qrscanner.redis.dto;

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
