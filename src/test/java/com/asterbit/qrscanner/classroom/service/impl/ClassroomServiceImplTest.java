package com.asterbit.qrscanner.classroom.service.impl;

import static com.asterbit.qrscanner.util.ConstMessages.TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.ActivityTimeRangeProperties;
import com.asterbit.qrscanner.activity.dto.ActivityDto;
import com.asterbit.qrscanner.activity.mapper.ActivityMapper;
import com.asterbit.qrscanner.activity.service.ActivityService;
import com.asterbit.qrscanner.checkins.CheckInRepository;
import com.asterbit.qrscanner.classroom.Classroom;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.exceptions.AlreadyCheckedInException;
import com.asterbit.qrscanner.exceptions.CheckInNotAllowedException;
import com.asterbit.qrscanner.exceptions.InvalidTokenException;
import com.asterbit.qrscanner.redis.CheckInToken;
import com.asterbit.qrscanner.redis.service.CheckinTokenService;
import com.asterbit.qrscanner.user.service.UserService;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceImplTest {

  @Mock
  private ActivityMapper activityMapper;
  @Mock
  private ClassroomRepository classRoomRepository;
  @Mock
  private CheckinTokenService checkinTokenService;
  @Mock
  private ActivityService activityService;
  @Mock
  private ActivityTimeRangeProperties timeRangeProperties;
  @Mock
  private CheckInRepository checkInRepository;
  @Mock
  private UserService userService;

  @InjectMocks
  private ClassroomServiceImpl classroomService;

  private UUID classroomId;
  private UUID userId;
  private LocalDateTime now;

  @BeforeEach
  void setup() {
    classroomId = UUID.randomUUID();
    userId = UUID.randomUUID();
    now = LocalDateTime.now();
  }
  @Test
  void currentActivities_classroomNotFound() {
    when(classRoomRepository.findById(classroomId)).thenReturn(Optional.empty());

    assertThatThrownBy(() -> classroomService.currentActivities(classroomId, userId))
        .isInstanceOf(RuntimeException.class); // ClassroomNotFoundException
  }
  @Test
  void currentActivities_success() {
    var classroom = Classroom.builder().id(classroomId).build();
    var activity = Activity.builder()
        .id(UUID.randomUUID())
        .classroom(classroom)
        .startTime(now.plusMinutes(15))
        .endTime(now.plusMinutes(60))
        .title("Math class")
        .build();
    var activityDto = new ActivityDto();
    activityDto.setId(activity.getId());
    activityDto.setTitle(activity.getTitle());

    var token = CheckInToken.builder().id("token-1").userId(userId).classroomId(classroomId).build();

    when(classRoomRepository.findById(classroomId)).thenReturn(Optional.of(classroom));
    when(activityService.findActivitiesStartingInRange(eq(classroomId), any(), any())).thenReturn(Set.of(activity));
    when(activityMapper.toDto(activity)).thenReturn(activityDto);
    when(checkinTokenService.createCheckInToken(any())).thenReturn(token);
    when(timeRangeProperties.getStartOffsetMinutes()).thenReturn(10L);
    when(timeRangeProperties.getEndOffsetHours()).thenReturn(2L);

    CurrentActivitiesDto result = classroomService.currentActivities(classroomId, userId);

    assertThat(result.getCheckInToken()).isEqualTo("token-1");
    assertThat(result.getActivities()).contains(activityDto);
    verify(checkinTokenService).createCheckInToken(any());
  }


  @Test
  void checkinStudent_alreadyCheckedIn() {
    var activityId = UUID.randomUUID();
    var dto = new CheckinStudentDto("token", activityId);

    when(activityService.findById(activityId)).thenReturn(Activity.builder().id(activityId).build());
    when(userService.isCheckedIn(userId, activityId)).thenReturn(true);

    assertThatThrownBy(() -> classroomService.checkinStudent(dto, userId))
        .isInstanceOf(AlreadyCheckedInException.class);
  }

  @Test
  void checkinStudent_checkInNotAllowed() {
    var start = now.minusMinutes(5);
    var activity = Activity.builder().id(UUID.randomUUID()).startTime(start).build();
    var dto = new CheckinStudentDto("token", activity.getId());

    when(activityService.findById(activity.getId())).thenReturn(activity);
    when(userService.isCheckedIn(userId, activity.getId())).thenReturn(false);
    when(timeRangeProperties.getStartOffsetMinutes()).thenReturn(10L);

    assertThatThrownBy(() -> classroomService.checkinStudent(dto, userId))
        .isInstanceOf(CheckInNotAllowedException.class);
  }

  @Test
  void checkinStudent_invalidToken() {
    var classroom = Classroom.builder().id(classroomId).build();
    var activity = Activity.builder()
        .id(UUID.randomUUID())
        .classroom(classroom)
        .startTime(now.plusMinutes(20))
        .endTime(now.plusMinutes(60))
        .build();
    var dto = new CheckinStudentDto("bad-token", activity.getId());

    when(activityService.findById(activity.getId())).thenReturn(activity);
    when(userService.isCheckedIn(userId, activity.getId())).thenReturn(false);
    when(timeRangeProperties.getStartOffsetMinutes()).thenReturn(10L);
    when(checkinTokenService.validateToken(dto.getToken(), classroom.getId(), userId)).thenReturn(false);

    assertThatThrownBy(() -> classroomService.checkinStudent(dto, userId))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessageContaining(TOKEN_NOT_FOUND);

    verify(checkinTokenService).validateToken(dto.getToken(), classroom.getId(), userId);
  }
}
