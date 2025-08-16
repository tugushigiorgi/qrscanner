package com.asterbit.qrscanner.classroom.service.impl;

import static com.asterbit.qrscanner.util.ConstMessages.ACTIVITIES_NOT_FOUND;
import static com.asterbit.qrscanner.util.ConstMessages.ACTIVITY_NOT_FOUND;
import static com.asterbit.qrscanner.util.ConstMessages.CLASSROOM_NOT_FOUND_WITH_ID;
import static com.asterbit.qrscanner.util.ConstMessages.TOKEN_NOT_FOUND;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.activity.ActivityTimeRangeProperties;
import com.asterbit.qrscanner.activity.dto.ActivityDto;
import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.activity.mapper.ActivityMapper;
import com.asterbit.qrscanner.checkins.CheckIn;
import com.asterbit.qrscanner.checkins.CheckInRepository;
import com.asterbit.qrscanner.classroom.Classroom;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.exceptions.InvalidTokenException;
import com.asterbit.qrscanner.redis.CheckInToken;
import com.asterbit.qrscanner.redis.CheckInTokenRepository;
import com.asterbit.qrscanner.redis.service.CheckinTokenService;
import com.asterbit.qrscanner.user.User;
import com.asterbit.qrscanner.user.UserRepository;
import com.asterbit.qrscanner.util.ConstMessages;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

@ExtendWith(MockitoExtension.class)
class ClassroomServiceImplTest {

  @Mock
  private ActivityMapper activityMapper;
  @Mock
  private ClassroomRepository classroomRepository;
  @Mock
  private CheckinTokenService checkinTokenService;
  @Mock
  private ActivityRepository activityRepository;
  @Mock
  private ActivityTimeRangeProperties timeRangeProperties;
  @Mock
  private CheckInTokenRepository checkInTokenRepository;
  @Mock
  private CheckInRepository checkInRepository;
  @Mock
  private UserRepository userRepository;

  @InjectMocks
  private ClassroomServiceImpl classroomService;

  @Test
  void currentActivities_success() {
    var classroomId = UUID.randomUUID();
    var userId = UUID.randomUUID();
    var classroom = Classroom.builder()
        .id(classroomId)
        .build();

    var activity = Activity.builder()
        .id(UUID.randomUUID())
        .classroom(classroom)
        .startTime(LocalDateTime.now().plusMinutes(15))
        .endTime(LocalDateTime.now().plusMinutes(60))
        .title("Math class")
        .build();
    var dto = ActivityDto.builder()
        .id(activity.getId())
        .title("Math class")
        .build();

    Set<Activity> activitySet = new HashSet<>();
    activitySet.add(activity);

    when(classroomRepository
        .findById(classroomId))
        .thenReturn(Optional.of(classroom));
    when(activityRepository
        .findActivitiesStartingInRange(any(), any(), any()))
        .thenReturn(activitySet);
    when(activityMapper.toDto(activity)).thenReturn(dto);

    var token = CheckInToken.builder()
        .id("token-1")
        .userId(userId)
        .classroomId(classroomId)
        .build();
    when(checkinTokenService
        .createCheckInToken(any()))
        .thenReturn(token);

    var result = classroomService.currentActivities(classroomId, userId);

    assertThat(result.getActivities()).contains(dto);
    assertThat(result.getCheckInToken()).isEqualTo("token-1");
  }

  @Test
  void currentActivities_classroomNotFound() {
    var classroomId = UUID.randomUUID();
    when(classroomRepository
        .findById(classroomId))
        .thenReturn(Optional.empty());

    assertThatThrownBy(() -> classroomService.currentActivities(classroomId, UUID.randomUUID()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(String.format(CLASSROOM_NOT_FOUND_WITH_ID, classroomId));
  }

  @Test
  void currentActivities_noActivitiesFound() {
    var classroomId = UUID.randomUUID();
    var classroom = Classroom.builder()
        .id(classroomId)
        .build();

    when(classroomRepository
        .findById(classroomId))
        .thenReturn(Optional.of(classroom));
    when(activityRepository
        .findActivitiesStartingInRange(any(), any(), any()))
        .thenReturn(Collections.emptySet());

    assertThatThrownBy(() -> classroomService.currentActivities(classroomId, UUID.randomUUID()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(String.format(ACTIVITIES_NOT_FOUND, classroomId));
  }

  @Test
  void checkinStudent_success() {
    var classroomId = UUID.randomUUID();
    var userId = UUID.randomUUID();

    var classroom = Classroom.builder()
        .id(classroomId)
        .name("Physics")
        .location("Lab 1")
        .build();

    var start = LocalDateTime.now().plusMinutes(30);
    var end = start.plusHours(1);

    var activity = Activity.builder()
        .id(UUID.randomUUID())
        .startTime(start)
        .endTime(end)
        .classroom(classroom)
        .checkIns(new HashSet<>())
        .build();

    var dto = new CheckinStudentDto("token-1", activity.getId());

    when(activityRepository
        .findById(activity.getId()))
        .thenReturn(Optional.of(activity));
    when(checkInTokenRepository
        .findById("token-1"))
        .thenReturn(Optional.of(
            CheckInToken.builder().id("token-1").classroomId(classroomId).userId(userId).build()));
    var user = User.builder()
        .id(userId)
        .firstName("Giorgi")
        .lastName("Tughushi")
        .email("j@d.com")
        .password("pwd")
        .checkins(new HashSet<>()).build();

    when(userRepository
        .findById(userId))
        .thenReturn(Optional.of(user));

    var savedCheckIn = CheckIn.builder()
        .id(UUID.randomUUID())
        .checkInDate(LocalDateTime.now().plusMinutes(10))
        .build();
    when(checkInRepository.save(any(CheckIn.class))).thenReturn(savedCheckIn);

    lenient().when(timeRangeProperties.getStartOffsetMinutes()).thenReturn(10L);
    lenient().when(timeRangeProperties.getEndOffsetHours()).thenReturn(2L);

    CheckinActivityDto result = classroomService.checkinStudent(dto, userId);

    assertThat(result.getClassroomName()).isEqualTo("Physics");
    assertThat(result.getClassroomLocation()).isEqualTo("Lab 1");
    assertThat(result.getActivityStart()).isEqualTo(start);
    assertThat(result.getActivityEnd()).isEqualTo(end);
    verify(checkInTokenRepository).delete(any());
  }


  @Test
  void checkinStudent_activityNotFound() {
    var dto = new CheckinStudentDto("token", UUID.randomUUID());
    when(activityRepository.findById(dto.getActivityId())).thenReturn(Optional.empty());

    assertThatThrownBy(() -> classroomService.checkinStudent(dto, UUID.randomUUID()))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(String.format(ACTIVITY_NOT_FOUND, dto.getActivityId()));
  }

  @Test
  void checkinStudent_invalidToken() {
    var classroom = Classroom.builder().id(UUID.randomUUID()).build();

    var start = LocalDateTime.now().plusMinutes(20);
    var activity = Activity.builder()
        .id(UUID.randomUUID())
        .startTime(start)
        .endTime(start.plusMinutes(60))
        .classroom(classroom)
        .checkIns(new HashSet<>())
        .build();

    var dto = new CheckinStudentDto("bad-token", activity.getId());

    when(activityRepository.findById(activity.getId())).thenReturn(Optional.of(activity));
    when(checkInTokenRepository.findById("bad-token")).thenReturn(Optional.empty());

    lenient().when(timeRangeProperties.getStartOffsetMinutes()).thenReturn(10L);
    lenient().when(timeRangeProperties.getEndOffsetHours()).thenReturn(2L);

    assertThatThrownBy(() -> classroomService.checkinStudent(dto, UUID.randomUUID()))
        .isInstanceOf(InvalidTokenException.class)
        .hasMessageContaining(TOKEN_NOT_FOUND);
  }

  @Test
  void checkinStudent_outsideWindow() {
    var classroom = Classroom.builder().id(UUID.randomUUID()).build();
    var start = LocalDateTime.now().minusMinutes(10);
    var activity = Activity.builder()
        .id(UUID.randomUUID())
        .startTime(start)
        .endTime(start.plusMinutes(60))
        .classroom(classroom)
        .checkIns(new HashSet<>())
        .build();

    var userId = UUID.randomUUID();
    var dto = new CheckinStudentDto("token", activity.getId());

    when(activityRepository.findById(activity.getId())).thenReturn(Optional.of(activity));

    assertThatThrownBy(() -> classroomService.checkinStudent(dto, userId))
        .isInstanceOf(ResponseStatusException.class)
        .hasMessageContaining(ConstMessages.CHECKIN_NOT_ALLOWED);
  }
}
