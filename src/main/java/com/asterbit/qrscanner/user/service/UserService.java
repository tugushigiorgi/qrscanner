package com.asterbit.qrscanner.user.service;

import com.asterbit.qrscanner.security.dto.JwtDto;
import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.asterbit.qrscanner.user.dto.UserDto;
import org.springframework.security.core.Authentication;

import java.util.UUID;

public interface UserService {
    JwtDto login(LoginDto loginDto);

    UserDto registerUser(RegisterUserDto dto);

    UUID currentUserId(Authentication authentication);
}
