package com.asterbit.qrscanner.redis.service;

import com.asterbit.qrscanner.redis.CheckInToken;
import java.util.Optional;
import java.util.UUID;

public interface CheckinTokenService {
  CheckInToken createCheckInToken(CheckInToken checkInToken);

  boolean validateToken(String tokenId, UUID classroomId, UUID currentUserId);
}