package com.asterbit.qrscanner.security;

import com.asterbit.qrscanner.security.dto.JwtDto;
import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.asterbit.qrscanner.user.dto.UserDto;
import com.asterbit.qrscanner.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication Controller", description = "Endpoints for login and register")
public class AuthController {

    private final UserService userService;

    @PostMapping("/login")
    @Operation(summary = "login with username and password")
    public ResponseEntity<JwtDto> login(
            @Parameter(description = "Login Dto which includes username and password")
            @Valid @RequestBody LoginDto loginDto) {
        return ResponseEntity.ok(userService.login(loginDto));
    }

    @PostMapping("/register")
    @Operation(summary = "register user")
    public ResponseEntity<UserDto> register(
            @Parameter(description = "Register Dto to register")
            @Valid @RequestBody RegisterUserDto userDto) {
        return ResponseEntity.ok(userService.registerUser(userDto));
    }
}
