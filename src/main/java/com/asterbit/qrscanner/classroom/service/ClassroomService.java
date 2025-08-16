package com.asterbit.qrscanner.classroom.service;

import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import java.util.Optional;
import java.util.UUID;

public interface ClassroomService {

  CurrentActivitiesDto currentActivities(UUID classroomId, UUID userId);

  CheckinActivityDto checkinStudent(CheckinStudentDto dto, UUID userId);

}
