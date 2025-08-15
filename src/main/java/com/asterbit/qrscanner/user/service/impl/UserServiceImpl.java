package com.asterbit.qrscanner.user.service.impl;

import com.asterbit.qrscanner.exceptions.EmailAlreadyExistsException;
import com.asterbit.qrscanner.exceptions.InvalidCredentialsException;
import com.asterbit.qrscanner.security.JwtFactory;
import com.asterbit.qrscanner.security.dto.JwtDto;
import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.User;
import com.asterbit.qrscanner.user.UserRepository;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.asterbit.qrscanner.user.dto.UserDto;
import com.asterbit.qrscanner.user.mapper.UserMapper;
import com.asterbit.qrscanner.user.service.UserService;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

import static com.asterbit.qrscanner.util.ConstMessages.*;
import static org.springframework.http.HttpStatus.BAD_REQUEST;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final AuthenticationManager authenticationManager;
    private final JwtFactory jwtFactory;
    private final PasswordEncoder passwordEncoder;
    private UserMapper userMapper;
    @Transactional(readOnly = true)
    public JwtDto login(LoginDto loginDto) {
        var currentUser = userRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(() -> new UsernameNotFoundException(
                        String.format(USER_NOT_FOUND_WITH_EMAIL, loginDto.getEmail())));
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDto.getEmail(), loginDto.getPassword()));
        } catch (BadCredentialsException ex) {
            throw new InvalidCredentialsException(INVALID_EMAIL_OR_PASSWORD);
        }
        var token = jwtFactory.generateToken(currentUser);
        return new JwtDto(token);
    }

    @Transactional
    public UserDto registerUser(RegisterUserDto dto) {
        var checkIfExists = userRepository.findByEmail(dto.email);
        if (checkIfExists.isPresent()) {
            throw  new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS);
        }
        var createUser = User.builder()
                .firstName(dto.name)
                .lastName(dto.surname)
                .email(dto.email)
                .password(passwordEncoder.encode(dto.password))
                .build();

        var entity= userRepository.save(createUser);
        return  userMapper.toDto(entity);
    }

//    @Override
//    public UUID currentUserId() {
//        if (authentication.getName() != null) {
//            var user = userRepository.findByEmail(authentication.getName());
//            if (user.isPresent()) {
//                log.info("User found with User ID: {}", user.get().getId());
//                return user.get().getId();
//            } else {
//                log.warn("No user found for email: {}", authentication.getName());
//            }
//        }
//
//        log.error("Failed to retrieve user ID: Authentication name is null or user not found.");
//        throw new ResponseStatusException(BAD_REQUEST, "User not found or invalid authentication");
//    }
}
