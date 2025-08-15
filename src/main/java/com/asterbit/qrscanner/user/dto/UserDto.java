package com.asterbit.qrscanner.user.dto;

import lombok.*;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class UserDto {
    public UUID id;
    public String firstName;
    public String lastName;
    public String email;
}
