package com.asterbit.qrscanner.util;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.activity.ActivityRepository;
import com.asterbit.qrscanner.classroom.Classroom;
import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.user.User;
import com.asterbit.qrscanner.user.UserRepository;
import com.asterbit.qrscanner.user.service.UserService;
import java.time.LocalDateTime;
import java.util.HashSet;
import lombok.AllArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@AllArgsConstructor
public class DataInitializer implements CommandLineRunner {

  private final ClassroomRepository classroomRepository;

  private final UserRepository userRepository;

  private final PasswordEncoder passwordEncoder;

  @Transactional
  @Override
  public void run(String... args) throws Exception {

    var user =User.builder()
        .email("test@gmail.com")
        .firstName("test")
        .lastName("test")
        .password(passwordEncoder.encode("test123"))
        .checkins(new HashSet<>())
        .build();
    userRepository.save(user);

    for (int i = 1; i <= 3; i++) {
      var activities = Activity.builder()
          .title("Introduction to Java - Session " + i)
          .description("Java basics for classroom " + i)
          .startTime(LocalDateTime.now().plusHours(24 + i * 2))
          .endTime(LocalDateTime.now().plusHours(29 + i * 2))
          .build();

      var newClassroom = Classroom.builder()
          .name("Classroom " + i)
          .location("SARTULI " + (2 + i))
          .activities(new HashSet<>())
          .build();

      newClassroom.addActivity(activities);
      activities.setClassroom(newClassroom);
      classroomRepository.save(newClassroom);
    }
  }
}
