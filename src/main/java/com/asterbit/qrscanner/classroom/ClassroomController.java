package com.asterbit.qrscanner.classroom;

import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.network.RequireSchoolWifi;
import com.asterbit.qrscanner.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classRoomService;
    private final UserService userService;

    @GetMapping("/{classroomId}/activities")
    @RequireSchoolWifi
    public ResponseEntity<CurrentActivitiesDto> currentActivities(Authentication authentication,@PathVariable UUID classroomId) {
        var currentUserId=userService.currentUserId(authentication);
        return ResponseEntity.ok(classRoomService.currentActivities(classroomId,currentUserId));
    }

    @PostMapping("/checkin")
    @RequireSchoolWifi
    public ResponseEntity<CheckinActivityDto> checkinStudent(Authentication authentication,@Valid @RequestBody CheckinStudentDto dto) {
        var currentUserId=userService.currentUserId(authentication);
        return ResponseEntity.ok(classRoomService.checkinStudent(dto,currentUserId));
    }
}
