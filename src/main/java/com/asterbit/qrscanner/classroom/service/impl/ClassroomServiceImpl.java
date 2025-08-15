package com.asterbit.qrscanner.classroom.service.impl;

import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.activity.ActivityTimeRangeProperties;
import com.asterbit.qrscanner.activity.mapper.ActivityMapper;
import com.asterbit.qrscanner.checkIntoken.CheckInToken;
import com.asterbit.qrscanner.checkIntoken.CheckInTokenRepository;
import com.asterbit.qrscanner.checkIntoken.service.CheckinTokenService;
import com.asterbit.qrscanner.checkins.CheckIn;
import com.asterbit.qrscanner.checkins.CheckInRepository;
import com.asterbit.qrscanner.checkins.dto.CheckinDto;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.exceptions.InvalidTokenException;
import com.asterbit.qrscanner.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.asterbit.qrscanner.util.ConstMessages.*;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@AllArgsConstructor
public class ClassroomServiceImpl implements ClassroomService {

    private final ActivityMapper activityMapper;
    private final ClassroomRepository classRoomRepository;
    private final CheckinTokenService checkinTokenService;
    private final ActivityRepository activityRepository;
    private final ActivityTimeRangeProperties timeRangeProperties;
    private final CheckInTokenRepository checkInTokenRepository;
    private final CheckInRepository checkInRepository;


    @Transactional
    @Override
    public CurrentActivitiesDto currentActivities(UUID classroomId) {
        //TODO
        var currentUserId = UUID.fromString(classroomId.toString());
        var classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, String.format(CLASSROOM_NOT_FOUND_WITH_ID, classroomId)));
        var now = LocalDateTime.now();
        var fromTime = now.plusMinutes(timeRangeProperties.getStartOffsetMinutes());
        var toTime = now.plusHours(timeRangeProperties.getEndOffsetHours());
        var currentActivities =
                activityRepository.findActivitiesStartingInRange(classroomId, fromTime, toTime);
        if (isEmpty(currentActivities)) {
            throw new ResponseStatusException(NOT_FOUND, String.format(ACTIVITIES_NOT_FOUND, classroomId));
        }
        var activitiesDto = currentActivities.stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toSet());
        var token = CheckInToken
                .builder()
                .classroomId(classroomId)
                .token(UUID.randomUUID())
                .userId(currentUserId)
                .build();
        var newCheckinToken = checkinTokenService.createCheckInToken(token);
        return CurrentActivitiesDto.builder()
                .activities(activitiesDto)
                .checkInToken(newCheckinToken.getToken())
                .build();
    }

    @Transactional
    @Override
    public CheckinDto CheckinStudent(UUID classroomId, UUID activityId) {
        var currentUserId = UUID.fromString(classroomId.toString());
        //TODO
        var currentUser = new User();
        var classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, String.format(CLASSROOM_NOT_FOUND_WITH_ID, classroomId)));
        var currentActivity = activityRepository.findById(activityId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, String.format(ACTIVITY_NOT_FOUND, classroomId)));
        if (!tokenValid(classroomId, currentUserId)) {
            throw new InvalidTokenException(String.format(TOKEN_NOT_FOUND, classroomId));
        }
        var newCheckin = CheckIn.builder()
                .user(currentUser)
                .checkInDate(LocalDateTime.now())
                .activity(currentActivity)
                .build();
        currentUser.addCheckIn(newCheckin);
        currentActivity.addCheckIn(newCheckin);
        var savedCheckin = checkInRepository.save(newCheckin);
        return CheckinDto.builder()
                .checkedIn(true)
                .checkinDate(savedCheckin.getCheckInDate())
                .activityStart(currentActivity.getStartTime())
                .activityEnd(currentActivity.getEndTime())
                .ClassroomName(classroom.getName())
                .ClassroomLocation(classroom.getLocation())
                .build();
    }

    private boolean tokenValid(UUID classroomId, UUID userId) {
        return checkInTokenRepository.findByUserId(userId)
                .map(token -> token.getToken().equals(classroomId))
                .orElse(false);
    }

}
