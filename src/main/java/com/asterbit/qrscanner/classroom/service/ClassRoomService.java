package com.asterbit.qrscanner.classroom.service;

import com.asterbit.qrscanner.activity.dto.ActivityDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;

import java.util.UUID;

public interface ClassRoomService {

    CurrentActivitiesDto currentActivities(UUID classroomId);

}
