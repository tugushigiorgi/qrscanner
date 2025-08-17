package com.asterbit.qrscanner.classroom;

import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.network.RequireSchoolWifi;
import com.asterbit.qrscanner.user.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
@Tag(name = "Classroom", description = "Endpoints for Classroom")
public class ClassroomController {

  private final ClassroomService classRoomService;
  private final UserService userService;

  @GetMapping("/{classroomId}/activities")
  @RequireSchoolWifi
  @Operation(summary = "Get current classroom activities")
  public ResponseEntity<CurrentActivitiesDto> currentActivities(Authentication authentication,
                                                                @Parameter(description = "ID of the classroom") @PathVariable UUID classroomId) {
    var currentUserId = userService.currentUserId(authentication);
    var activities = classRoomService.currentActivities(classroomId, currentUserId);


    return ResponseEntity.ok(activities);
  }

  @PostMapping("/checkin")
  @RequireSchoolWifi
  @Operation(summary = "Check in classroom activity")
  public ResponseEntity<CheckinActivityDto> checkinStudent(Authentication authentication, @Parameter(description = "Checkin Student Dto ") @Valid @RequestBody CheckinStudentDto dto) {
    var currentUserId = userService.currentUserId(authentication);
    return ResponseEntity.ok(classRoomService.checkinStudent(dto, currentUserId));
  }
}
