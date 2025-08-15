package com.asterbit.qrscanner.classroom.service.impl;

import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.activity.ActivityTimeRangeProperties;
import com.asterbit.qrscanner.activity.mapper.ActivityMapper;
import com.asterbit.qrscanner.checkIntoken.CheckInToken;
import com.asterbit.qrscanner.checkIntoken.CheckInTokenRepository;
import com.asterbit.qrscanner.checkIntoken.service.CheckinTokenService;
import com.asterbit.qrscanner.checkins.CheckIn;
import com.asterbit.qrscanner.checkins.CheckInRepository;
import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.exceptions.InvalidTokenException;
import com.asterbit.qrscanner.user.User;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.asterbit.qrscanner.util.ConstMessages.*;
import static org.springframework.http.HttpStatus.FORBIDDEN;
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
     //TO DO
        var currentUserId = UUID.randomUUID();
        var classroom = classRoomRepository.findById(classroomId)
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND, String.format(CLASSROOM_NOT_FOUND_WITH_ID, classroomId)));

        var now = LocalDateTime.now();
        var fromTime = now.minusMinutes(timeRangeProperties.getStartOffsetMinutes());
        var toTime = now.plusHours(timeRangeProperties.getEndOffsetHours());
        var currentActivities = activityRepository.findActivitiesStartingInRange(classroomId, fromTime, toTime);
        if (isEmpty(currentActivities)) {
            throw new ResponseStatusException(NOT_FOUND, String.format(ACTIVITIES_NOT_FOUND, classroomId));
        }
        var activitiesDto = currentActivities.stream()
                .map(activityMapper::toDto)
                .collect(Collectors.toSet());

        var token = CheckInToken.builder()
                .classroomId(classroomId)
                .userId(currentUserId)
                .build();
        var newCheckinToken = checkinTokenService.createCheckInToken(token);

        return CurrentActivitiesDto.builder()
                .activities(activitiesDto)
                .checkInToken(newCheckinToken.getId())
                .build();
    }

    @Transactional
    @Override
    public CheckinActivityDto checkinStudent(CheckinStudentDto dto) {
        //TODO
        var currentUserId = UUID.randomUUID();
        var currentUser = User.builder().id(currentUserId)
                .checkins(new HashSet<>())
                .build();
        var currentActivity = activityRepository.findById(dto.getActivityId())
                .orElseThrow(() -> new ResponseStatusException(NOT_FOUND,
                        String.format(ACTIVITY_NOT_FOUND, dto.getActivityId())));

        var now = LocalDateTime.now();
        var windowStart = currentActivity.getStartTime().minusMinutes(timeRangeProperties.getStartOffsetMinutes());
        var windowEnd = currentActivity.getStartTime();

        if (now.isBefore(windowStart) || now.isAfter(windowEnd)) {
            throw new ResponseStatusException(FORBIDDEN, CHECKIN_NOT_ALLOWED);
        }
        var classroom = currentActivity.getClassroom();
        if (!isTokenValid(dto.getToken(), classroom.getId(), currentUserId)) {
            throw new InvalidTokenException(TOKEN_NOT_FOUND);
        }
        var newCheckin = CheckIn.builder()
                .checkInDate(LocalDateTime.now())
                .build();
        currentUser.addCheckIn(newCheckin);
        currentActivity.addCheckIn(newCheckin);
        var savedCheckin = checkInRepository.save(newCheckin);
        return CheckinActivityDto.builder()
                .checkinDate(savedCheckin.getCheckInDate())
                .activityStart(currentActivity.getStartTime())
                .activityEnd(currentActivity.getEndTime())
                .ClassroomName(classroom.getName())
                .ClassroomLocation(classroom.getLocation())
                .build();
    }

    private boolean isTokenValid(String tokenId, UUID classroomId, UUID currentUserId) {
        return checkInTokenRepository.findById(tokenId)
                .map(token -> {
                    var matchesClassroom = classroomId.equals(token.getClassroomId());
                    var matchesUser = currentUserId.equals(token.getUserId());
                    if (matchesClassroom && matchesUser) {
                        checkInTokenRepository.delete(token);
                        return true;
                    }
                    return false;
                })
                .orElse(false);
    }

}
