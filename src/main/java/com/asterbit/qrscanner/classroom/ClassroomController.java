package com.asterbit.qrscanner.classroom;

import com.asterbit.qrscanner.activity.dto.CheckinActivityDto;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.network.RequireSchoolWifi;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classRoomService;

    @GetMapping("/{classroomId}/activities")
    @RequireSchoolWifi
    public ResponseEntity<CurrentActivitiesDto> currentActivities(@PathVariable UUID classroomId) {
        return ResponseEntity.ok(classRoomService.currentActivities(classroomId));
    }

    @PostMapping("/checkin")
    @RequireSchoolWifi
    public ResponseEntity<CheckinActivityDto> checkinStudent(@Valid @RequestBody CheckinStudentDto dto) {
        return ResponseEntity.ok(classRoomService.checkinStudent(dto));
    }
}
