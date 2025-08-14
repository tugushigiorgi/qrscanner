package com.asterbit.qrscanner.checkIntoken.service.impl;

import com.asterbit.qrscanner.checkIntoken.CheckInToken;
import com.asterbit.qrscanner.checkIntoken.CheckInTokenRepository;
import com.asterbit.qrscanner.checkIntoken.service.CheckinTokenService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;

import static com.asterbit.qrscanner.util.ConstMessages.*;
import static org.slf4j.helpers.Reporter.info;

@Service
@AllArgsConstructor
@Slf4j
public class CheckinTokenServiceImpl implements CheckinTokenService {

    private final CheckInTokenRepository checkInTokenRepository;

    @Override
    public CheckInToken createCheckInToken(CheckInToken checkInToken) {
        info(String.format(CREATING_CHECKIN_TOKEN));
        return checkInTokenRepository.save(checkInToken);
    }

    @Override
    public Optional<CheckInToken> getCheckInTokenById(String id) {
        info(String.format(CREATING_CHECKIN_TOKEN_BY_ID,id));
        return checkInTokenRepository.findById(id);
    }

    @Override
    public void deleteCheckInTokenById(String id) {
        info(String.format(DELETING_CHECKIN_TOKEN_BY_ID,id));
        checkInTokenRepository.deleteById(id);
        info(String.format(CHECKIN_TOKEN_DELETED,id));
    }
}
