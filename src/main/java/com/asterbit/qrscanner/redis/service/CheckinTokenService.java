package com.asterbit.qrscanner.redis.service;

import com.asterbit.qrscanner.redis.CheckInToken;
import java.util.Optional;

public interface CheckinTokenService {
  CheckInToken createCheckInToken(CheckInToken checkInToken);

  Optional<CheckInToken> getCheckInTokenById(String id);

  void deleteCheckInTokenById(String id);
}