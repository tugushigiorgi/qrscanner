package com.asterbit.qrscanner.activity.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.exceptions.ActivityNotFoundException;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

class ActivityServiceImplTest {

  @Mock
  private ActivityRepository activityRepository;

  @InjectMocks
  private ActivityServiceImpl activityService;

  @BeforeEach
  void setUp() {
    MockitoAnnotations.openMocks(this);
  }

  @Test
  void findById_existingActivity_returnsActivity() {
    var activityId = UUID.randomUUID();
    var activity = new Activity();
    activity.setId(activityId);

    when(activityRepository.findById(activityId)).thenReturn(Optional.of(activity));

    var result = activityService.findById(activityId);

    assertNotNull(result);
    assertEquals(activityId, result.getId());
    verify(activityRepository, times(1)).findById(activityId);
  }

  @Test
  void findById_nonExistingActivity_throwsException() {
    var activityId = UUID.randomUUID();

    when(activityRepository.findById(activityId)).thenReturn(Optional.empty());

    ActivityNotFoundException exception = assertThrows(
        ActivityNotFoundException.class,
        () -> activityService.findById(activityId)
    );

    assertEquals(activityId, exception.getActivityId());
    verify(activityRepository, times(1)).findById(activityId);
  }

  @Test
  void findActivitiesStartingInRange_returnsActivities() {
    var classroomId = UUID.randomUUID();
    var from = LocalDateTime.now();
    var to = from.plusHours(1);

    var activity1 = new Activity();
    var activity2 = new Activity();

    var activities = Set.of(activity1, activity2);

    when(activityRepository.findActivitiesStartingInRange(classroomId, from, to))
        .thenReturn(activities);

    var result = activityService.findActivitiesStartingInRange(classroomId, from, to);

    assertNotNull(result);
    assertEquals(2, result.size());
    verify(activityRepository, times(1)).findActivitiesStartingInRange(classroomId, from, to);
  }
}
