package com.asterbit.qrscanner.activity.service.impl;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.activity.service.ActivityService;
import com.asterbit.qrscanner.exceptions.ActivityNotFoundException;
import java.time.LocalDateTime;
import java.util.Set;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ActivityServiceImpl implements ActivityService {
  private final ActivityRepository activityRepository;

  @Transactional(readOnly = true)
  @Override
  public Activity findById(UUID activityId) {
    return activityRepository.findById(activityId)
        .orElseThrow(() -> new ActivityNotFoundException(activityId));
  }

  @Transactional(readOnly = true)
  @Override
  public Set<Activity> findActivitiesStartingInRange(UUID classroomId, LocalDateTime fromTime, LocalDateTime toTime) {
    return activityRepository.findActivitiesStartingInRange(classroomId, fromTime, toTime);
  }
}
