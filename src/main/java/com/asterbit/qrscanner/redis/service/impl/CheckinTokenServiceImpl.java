package com.asterbit.qrscanner.redis.service.impl;

import com.asterbit.qrscanner.redis.CheckInToken;
import com.asterbit.qrscanner.redis.CheckInTokenRepository;
import com.asterbit.qrscanner.redis.service.CheckinTokenService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
@AllArgsConstructor
public class CheckinTokenServiceImpl implements CheckinTokenService {

    private final CheckInTokenRepository checkInTokenRepository;

    @Override
    public CheckInToken createCheckInToken(CheckInToken checkInToken) {
        return checkInTokenRepository.save(checkInToken);
    }

    @Override
    public Optional<CheckInToken> getCheckInTokenById(String id) {
        return checkInTokenRepository.findById(id);
    }

    @Override
    public void deleteCheckInTokenById(String id) {
        checkInTokenRepository.deleteById(id);
    }
}
