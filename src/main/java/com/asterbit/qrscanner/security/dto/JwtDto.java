package com.asterbit.qrscanner.security.dto;

import lombok.*;

@Builder
@Data
@Getter
@Setter
@AllArgsConstructor
public class JwtDto {
    public String token;
}