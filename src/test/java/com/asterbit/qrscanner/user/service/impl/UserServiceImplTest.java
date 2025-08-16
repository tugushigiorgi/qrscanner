package com.asterbit.qrscanner.user.service.impl;

import static com.asterbit.qrscanner.util.ConstMessages.AUTHENTICATION_INVALID;
import static com.asterbit.qrscanner.util.ConstMessages.EMAIL_ALREADY_EXISTS;
import static com.asterbit.qrscanner.util.ConstMessages.INVALID_EMAIL_OR_PASSWORD;
import static com.asterbit.qrscanner.util.ConstMessages.USER_NOT_FOUND_WITH_EMAIL;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.asterbit.qrscanner.exceptions.EmailAlreadyExistsException;
import com.asterbit.qrscanner.exceptions.InvalidCredentialsException;
import com.asterbit.qrscanner.security.JwtFactory;
import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.User;
import com.asterbit.qrscanner.user.UserRepository;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.asterbit.qrscanner.user.dto.UserDto;
import com.asterbit.qrscanner.user.mapper.UserMapper;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(org.mockito.junit.jupiter.MockitoExtension.class)
class UserServiceImplTest {

  @Mock
  private UserRepository userRepository;

  @Mock
  private AuthenticationManager authenticationManager;

  @Mock
  private JwtFactory jwtFactory;

  @Mock
  private UserMapper userMapper;

  @Mock
  private org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

  @InjectMocks
  private UserServiceImpl userService;

  private UUID userId;
  private User user;

  @BeforeEach
  void setUp() {
    userId = UUID.randomUUID();
    user = User.builder()
        .id(userId)
        .firstName("John")
        .lastName("Doe")
        .email("john@example.com")
        .password("encodedPassword")
        .build();
  }

  // ===== LOGIN SUCCESS =====
  @Test
  void login_success() {
    var loginDto = new LoginDto("john@example.com", "password");

    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
    when(jwtFactory.generateToken(user)).thenReturn("fake-jwt-token");

    var jwtDto = userService.login(loginDto);

    assertThat(jwtDto).isNotNull();
    assertThat(jwtDto.getToken()).isEqualTo("fake-jwt-token");

    verify(authenticationManager).authenticate(
        any(UsernamePasswordAuthenticationToken.class)
    );
  }

  // ===== LOGIN FAILURE - USER NOT FOUND =====
  @Test
  void login_userNotFound() {
    var loginDto = new LoginDto("missing@example.com", "password");

    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.login(loginDto))
        .isInstanceOf(UsernameNotFoundException.class)
        .hasMessageContaining(String.format(USER_NOT_FOUND_WITH_EMAIL, loginDto.getEmail()));
  }

  // ===== LOGIN FAILURE - INVALID CREDENTIALS =====
  @Test
  void login_invalidCredentials() {
    var loginDto = new LoginDto("john@example.com", "wrongpassword");

    when(userRepository.findByEmail(loginDto.getEmail())).thenReturn(Optional.of(user));
    doThrow(BadCredentialsException.class)
        .when(authenticationManager)
        .authenticate(any(UsernamePasswordAuthenticationToken.class));

    assertThatThrownBy(() -> userService.login(loginDto))
        .isInstanceOf(InvalidCredentialsException.class)
        .hasMessageContaining(INVALID_EMAIL_OR_PASSWORD);
  }

  // ===== REGISTER SUCCESS =====
  @Test
  void registerUser_success() {
    var dto = RegisterUserDto.builder()
        .name("John")
        .surname("Doe")
        .email("john@example.com")
        .password("password")
        .build();

    when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.empty());
    when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
    when(userRepository.save(any(User.class))).thenReturn(user);
    when(userMapper.toDto(user)).thenReturn(new UserDto(userId, "John", "Doe", "john@example.com"));

    var result = userService.registerUser(dto);

    assertThat(result).isNotNull();
    assertThat(result.getId()).isEqualTo(userId);
    assertThat(result.getEmail()).isEqualTo("john@example.com");
  }

  // ===== REGISTER FAILURE - EMAIL EXISTS =====
  @Test
  void registerUser_emailExists() {
    var dto = RegisterUserDto.builder()
        .name("John")
        .surname("Doe")
        .email("john@example.com")
        .password("password")
        .build();

    when(userRepository.findByEmail(dto.getEmail())).thenReturn(Optional.of(user));

    assertThatThrownBy(() -> userService.registerUser(dto))
        .isInstanceOf(EmailAlreadyExistsException.class)
        .hasMessageContaining(EMAIL_ALREADY_EXISTS);
  }

  // ===== CURRENT USER ID SUCCESS =====
  @Test
  void currentUserId_success() {
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn(user.getEmail());
    when(userRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));

    UUID id = userService.currentUserId(auth);

    assertThat(id).isEqualTo(userId);
  }

  // ===== CURRENT USER ID FAILURE =====
  @Test
  void currentUserId_invalidAuthentication() {
    assertThatThrownBy(() -> userService.currentUserId(null))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(AUTHENTICATION_INVALID);
  }

  @Test
  void currentUserId_userNotFound() {
    Authentication auth = mock(Authentication.class);
    when(auth.getName()).thenReturn("missing@example.com");
    when(userRepository.findByEmail("missing@example.com")).thenReturn(Optional.empty());

    assertThatThrownBy(() -> userService.currentUserId(auth))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(String.format(USER_NOT_FOUND_WITH_EMAIL, "missing@example.com"));
  }
}
