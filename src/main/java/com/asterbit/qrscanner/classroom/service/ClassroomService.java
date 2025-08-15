package com.asterbit.qrscanner.classroom.service;

import com.asterbit.qrscanner.checkins.dto.CheckinDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;

import java.util.UUID;

public interface ClassroomService {

    CurrentActivitiesDto currentActivities(UUID classroomId);

    CheckinDto checkinStudent(UUID classroomId, UUID activityId);

}
