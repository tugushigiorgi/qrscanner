package com.asterbit.qrscanner.classroom;

import com.asterbit.qrscanner.checkins.dto.CheckinDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.classroom.service.ClassroomService;
import com.asterbit.qrscanner.network.RequireSchoolWifi;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController("/classroom")
@RequiredArgsConstructor
public class ClassroomController {

    private final ClassroomService classRoomService;

    @GetMapping("/{classroomId}/activities")
    @RequireSchoolWifi
    public ResponseEntity<CurrentActivitiesDto> currentActivities(@PathVariable UUID classroomId) {
        return ResponseEntity.ok(classRoomService.currentActivities(classroomId));
    }

    @PostMapping("/{classroomId}/checkin/{activityId}")
    @RequireSchoolWifi
    public ResponseEntity<CheckinDto> checkinStudent(@PathVariable UUID classroomId, @PathVariable UUID activityId) {
        return ResponseEntity.ok(classRoomService.CheckinStudent(classroomId, activityId));
    }
}
