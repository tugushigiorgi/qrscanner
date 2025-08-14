package com.asterbit.qrscanner.classroom.service.impl;

import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.activity.ActivityTimeRangeProperties;
import com.asterbit.qrscanner.activity.mapper.ActivityMapper;
import com.asterbit.qrscanner.checkIntoken.CheckInToken;
import com.asterbit.qrscanner.checkIntoken.service.CheckinTokenService;
import com.asterbit.qrscanner.classroom.ClassRoomRepository;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassRoomService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.stream.Collectors;

import static com.asterbit.qrscanner.util.ConstMessages.ACTIVITIES_NOT_FOUND;
import static com.asterbit.qrscanner.util.ConstMessages.CLASSROOM_NOT_FOUND_WITH_ID;
import static org.springframework.http.HttpStatus.NOT_FOUND;
import static org.springframework.util.CollectionUtils.isEmpty;

@Service
@AllArgsConstructor
public class ClassroomServiceImpl implements ClassRoomService {

    private final ActivityMapper activityMapper;
    private final ClassRoomRepository classRoomRepository;
    private final CheckinTokenService checkinTokenService;
    private final ActivityRepository activityRepository;
    private final ActivityTimeRangeProperties timeRangeProperties;

    @Transactional
    @Override
    public CurrentActivitiesDto currentActivities(UUID classroomId) {
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


}
