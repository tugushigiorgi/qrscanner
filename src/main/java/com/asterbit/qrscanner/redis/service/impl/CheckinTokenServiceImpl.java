package com.asterbit.qrscanner.redis.service.impl;

import com.asterbit.qrscanner.redis.CheckInToken;
import com.asterbit.qrscanner.redis.CheckInTokenRepository;
import com.asterbit.qrscanner.redis.service.CheckinTokenService;
import java.util.Optional;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
@Slf4j
public class CheckinTokenServiceImpl implements CheckinTokenService {

  private final CheckInTokenRepository checkInTokenRepository;

  @Override
  public CheckInToken createCheckInToken(CheckInToken checkInToken) {
    return checkInTokenRepository.save(checkInToken);
  }

  @Override
  public boolean validateToken(String tokenId, UUID classroomId, UUID currentUserId) {
    log.debug("Validating token={} for userId={} and classroomId={}", tokenId, currentUserId, classroomId);

    return checkInTokenRepository.findById(tokenId)
        .map(token -> {
          var matchesClassroom = classroomId.equals(token.getClassroomId());
          var matchesUser = currentUserId.equals(token.getUserId());
          if (matchesClassroom && matchesUser) {
            log.info("Token {} is valid for userId={} and classroomId={}", tokenId, currentUserId, classroomId);
            checkInTokenRepository.delete(token);
            return true;
          }
          log.warn("Token {} validation failed: classroomMatch={} userMatch={}", tokenId, matchesClassroom, matchesUser);
          return false;
        })
        .orElse(false);
  }
}
