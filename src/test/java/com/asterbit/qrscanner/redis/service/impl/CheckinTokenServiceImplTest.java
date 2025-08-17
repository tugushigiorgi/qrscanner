package com.asterbit.qrscanner.redis.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.asterbit.qrscanner.redis.CheckInToken;
import com.asterbit.qrscanner.redis.CheckInTokenRepository;
import java.util.Optional;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class CheckinTokenServiceImplTest {

  @Mock
  private CheckInTokenRepository checkInTokenRepository;

  @InjectMocks
  private CheckinTokenServiceImpl checkinTokenService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void createCheckInToken_savesAndReturnsToken() {
    var token = CheckInToken.builder()
        .id("token123")
        .userId(UUID.randomUUID())
        .classroomId(UUID.randomUUID())
        .build();

    when(checkInTokenRepository.save(token)).thenReturn(token);

    var result = checkinTokenService.createCheckInToken(token);

    assertNotNull(result);
    assertEquals("token123", result.getId());
    verify(checkInTokenRepository, times(1)).save(token);
  }

  @Test
  void validateToken_existingTokenWithMatchingIds_returnsTrueAndDeletesToken() {
    var tokenId = "token123";
    var classroomId = UUID.randomUUID();
    var userId = UUID.randomUUID();

    var token = CheckInToken.builder()
        .id(tokenId)
        .classroomId(classroomId)
        .userId(userId)
        .build();

    when(checkInTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

    var valid = checkinTokenService.validateToken(tokenId, classroomId, userId);

    assertTrue(valid);
    verify(checkInTokenRepository, times(1)).delete(token);
    verify(checkInTokenRepository, times(1)).findById(tokenId);
  }

  @Test
  void validateToken_existingTokenWithNonMatchingIds_returnsFalseAndDoesNotDelete() {
    var tokenId = "token123";
    var classroomId = UUID.randomUUID();
    var userId = UUID.randomUUID();

    var token = CheckInToken.builder()
        .id(tokenId)
        .classroomId(UUID.randomUUID())
        .userId(UUID.randomUUID())
        .build();

    when(checkInTokenRepository.findById(tokenId)).thenReturn(Optional.of(token));

    var valid = checkinTokenService.validateToken(tokenId, classroomId, userId);

    assertFalse(valid);
    verify(checkInTokenRepository, never()).delete(token);
    verify(checkInTokenRepository, times(1)).findById(tokenId);
  }

  @Test
  void validateToken_nonExistingToken_returnsFalse() {
    var tokenId = "token123";
    var classroomId = UUID.randomUUID();
    var userId = UUID.randomUUID();

    when(checkInTokenRepository.findById(tokenId)).thenReturn(Optional.empty());

    var valid = checkinTokenService.validateToken(tokenId, classroomId, userId);

    assertFalse(valid);
    verify(checkInTokenRepository, never()).delete(any());
    verify(checkInTokenRepository, times(1)).findById(tokenId);
  }
}
