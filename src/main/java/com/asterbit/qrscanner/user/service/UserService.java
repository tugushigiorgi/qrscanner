package com.asterbit.qrscanner.user.service;

import com.asterbit.qrscanner.security.dto.JwtDto;
import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.asterbit.qrscanner.user.dto.UserDto;

public interface UserService {
     JwtDto login(LoginDto loginDto);
     UserDto registerUser(RegisterUserDto dto);
}
