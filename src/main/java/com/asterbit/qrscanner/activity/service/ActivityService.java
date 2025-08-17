package com.asterbit.qrscanner.activity.service;

import com.asterbit.qrscanner.activity.Activity;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;

public interface ActivityService {

  Activity findById(UUID activityId);

  Set<Activity> findActivitiesStartingInRange(
      UUID classroomId,
      LocalDateTime fromTime,
      LocalDateTime toTime
  );
}
