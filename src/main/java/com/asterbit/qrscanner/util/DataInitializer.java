package com.asterbit.qrscanner.util;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.classroom.Classroom;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.user.User;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.HashSet;

@Component
@AllArgsConstructor
public class DataInitializer  implements CommandLineRunner {

    private final ClassroomRepository classroomRepository;
    private final ActivityRepository activityRepository;


    @Override
    public void run(String... args) throws Exception {

        var activities =Activity.builder()
                .title("Activitie 3")
                .description("Activities are used for testing purposes")
                .startTime(LocalDateTime.now().plusMinutes(10))
                .endTime(LocalDateTime.now().plusHours(3))
                .build();


        var newclassroom = Classroom.builder()
                .name("New Classroom 3")
                .location("New Location 3")
                .activities(new HashSet<>())
                .build();
        newclassroom.addActivity(activities);
        activities.setClassroom(newclassroom);
        classroomRepository.save(newclassroom);




    }
}
