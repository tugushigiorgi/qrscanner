package com.asterbit.qrscanner.classroom.service.impl;

import static com.asterbit.qrscanner.util.ConstMessages.TOKEN_NOT_FOUND;

import com.asterbit.qrscanner.activity.ActivityTimeRangeProperties;
import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.activity.mapper.ActivityMapper;
import com.asterbit.qrscanner.activity.service.ActivityService;
import com.asterbit.qrscanner.checkins.CheckIn;
import com.asterbit.qrscanner.checkins.CheckInRepository;
import com.asterbit.qrscanner.classroom.Classroom;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.exceptions.AlreadyCheckedInException;
import com.asterbit.qrscanner.exceptions.CheckInNotAllowedException;
import com.asterbit.qrscanner.exceptions.ClassroomNotFoundException;
import com.asterbit.qrscanner.exceptions.InvalidTokenException;
import com.asterbit.qrscanner.redis.CheckInToken;
import com.asterbit.qrscanner.redis.service.CheckinTokenService;
import com.asterbit.qrscanner.user.service.UserService;
import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j

public class ClassroomServiceImpl implements ClassroomService {

  private final ActivityMapper activityMapper;
  private final ClassroomRepository classRoomRepository;
  private final CheckinTokenService checkinTokenService;
  private final ActivityService activityService;
  private final ActivityTimeRangeProperties timeRangeProperties;
  private final CheckinTokenService tokenService;
  private final CheckInRepository checkInRepository;
  private final UserService userService;
  private final LocalDateTime now = LocalDateTime.now();

  @Transactional(readOnly = true)
  @Override
  public CurrentActivitiesDto currentActivities(UUID classroomId, UUID userId) {
    log.debug("Fetching current activities for classroomId={} and userId={}", classroomId, userId);

    var classroom = findById(classroomId);

    var fromTime = now.minusMinutes(timeRangeProperties.getStartOffsetMinutes());
    var toTime = now.plusHours(timeRangeProperties.getEndOffsetHours());

    var currentActivities = activityService.findActivitiesStartingInRange(classroomId, fromTime, toTime);

    var activitiesDto = currentActivities.stream()
        .map(activityMapper::toDto)
        .collect(Collectors.toSet());

    String checkInTokenId = null;
    if (!activitiesDto.isEmpty()) {
      var token = CheckInToken.builder()
          .classroomId(classroomId)
          .userId(userId)
          .build();

      var newCheckinToken = checkinTokenService.createCheckInToken(token);
      checkInTokenId = newCheckinToken.getId();
      log.info("Generated check-in token {} for classroomId={} and userId={}", checkInTokenId, classroomId, userId);
    }

    return CurrentActivitiesDto.builder()
        .activities(activitiesDto)
        .checkInToken(checkInTokenId)
        .build();
  }

  @Transactional
  @Override
  public CheckinActivityDto checkinStudent(CheckinStudentDto dto, UUID userId) {
    log.debug("User {} attempting check-in for activity {}", userId, dto.getActivityId());

    var currentActivity = activityService.findById(dto.getActivityId());
    var alreadyCheckedIn = userService.isCheckedIn(userId, dto.getActivityId());

    if (alreadyCheckedIn) {
      throw new AlreadyCheckedInException(dto.getActivityId());
    }

    var latestAllowed = currentActivity.getStartTime()
        .minusMinutes(timeRangeProperties.getStartOffsetMinutes());

    if (now.isAfter(latestAllowed)) {
      throw new CheckInNotAllowedException(latestAllowed);
    }

    var classroom = currentActivity.getClassroom();
    if (!tokenService.validateToken(dto.getToken(), classroom.getId(), userId)) {
      throw new InvalidTokenException(TOKEN_NOT_FOUND);
    }

    var currentUser = userService.findById(userId);

    var newCheckin = CheckIn.builder()
        .checkInDate(LocalDateTime.now())
        .build();

    currentUser.addCheckIn(newCheckin);
    currentActivity.addCheckIn(newCheckin);

    var savedCheckin = checkInRepository.save(newCheckin);

    log.info("User {} successfully checked into activity {} at {}", userId, currentActivity.getId(), savedCheckin.getCheckInDate());

    return CheckinActivityDto.builder()
        .checkinDate(savedCheckin.getCheckInDate())
        .activityStart(currentActivity.getStartTime())
        .activityEnd(currentActivity.getEndTime())
        .ClassroomName(classroom.getName())
        .ClassroomLocation(classroom.getLocation())
        .build();
  }

  @Override
  public Classroom findById(UUID classroomId) {
    return classRoomRepository.findById(classroomId)
        .orElseThrow(() -> new ClassroomNotFoundException(classroomId));
  }
}
