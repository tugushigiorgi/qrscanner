package com.asterbit.qrscanner.user.service.impl;

import static com.asterbit.qrscanner.util.ConstMessages.AUTHENTICATION_INVALID;
import static com.asterbit.qrscanner.util.ConstMessages.EMAIL_ALREADY_EXISTS;
import static com.asterbit.qrscanner.util.ConstMessages.INVALID_EMAIL_OR_PASSWORD;
import static com.asterbit.qrscanner.util.ConstMessages.USER_NOT_FOUND_WITH_EMAIL;
import static com.asterbit.qrscanner.util.ConstMessages.USER_WITH_ID_NOT_FOUND;
import static org.springframework.http.HttpStatus.BAD_REQUEST;

import com.asterbit.qrscanner.checkins.CheckInRepository;
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
import java.util.HashSet;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.aspectj.weaver.ast.Var;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

  private final UserRepository userRepository;
  private final AuthenticationManager authenticationManager;
  private final JwtFactory jwtFactory;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final CheckInRepository checkInRepository;

  @Transactional(readOnly = true)
  @Override
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
  @Override
  public UserDto registerUser(RegisterUserDto dto) {
    var checkIfExists = userRepository.findByEmail(dto.email);
    if (checkIfExists.isPresent()) {
      throw new EmailAlreadyExistsException(EMAIL_ALREADY_EXISTS);
    }
    var createUser = User.builder()
        .firstName(dto.name)
        .lastName(dto.surname)
        .checkins(new HashSet<>())
        .email(dto.email)
        .password(passwordEncoder.encode(dto.password))
        .build();

    var entity = userRepository.save(createUser);
    return userMapper.toDto(entity);
  }

  @Transactional(readOnly = true)
  @Override
  public UUID currentUserId(Authentication authentication) {
    if (authentication == null || authentication.getName() == null) {
      throw new ResponseStatusException(BAD_REQUEST, AUTHENTICATION_INVALID);
    }
    return userRepository.findByEmail(authentication.getName())
        .map(User::getId)
        .orElseThrow(() -> new ResponseStatusException(
            BAD_REQUEST,
            String.format(USER_NOT_FOUND_WITH_EMAIL, authentication.getName())
        ));
  }

  @Override
  @Transactional(readOnly = true)
  public Boolean isCheckedIn(UUID userId, UUID activityId) {
    return checkInRepository.existsByUserAndActivity(userId,activityId);
  }

  @Override
  public User findById(UUID userId) {
    return userRepository.findById(userId)
        .orElseThrow(() -> new ResponseStatusException(BAD_REQUEST));

  }
}
