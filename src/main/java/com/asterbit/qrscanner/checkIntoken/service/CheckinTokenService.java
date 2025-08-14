package com.asterbit.qrscanner.checkIntoken.service;

import com.asterbit.qrscanner.checkIntoken.CheckInToken;

import java.util.Optional;
import java.util.UUID;

public interface CheckinTokenService {
    CheckInToken createCheckInToken(CheckInToken checkInToken);
    Optional<CheckInToken> getCheckInTokenById(String id);
    void deleteCheckInTokenById(String id);
}