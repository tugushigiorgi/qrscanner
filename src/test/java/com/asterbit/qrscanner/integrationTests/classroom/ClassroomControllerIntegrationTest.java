package com.asterbit.qrscanner.integrationTests.classroom;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.asterbit.qrscanner.classroom.ClassroomRepository;
import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.asterbit.qrscanner.user.service.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ClassroomControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  @Autowired
  private ClassroomRepository classRoomRepository;

  private UUID dbClassroomId;
  private String jwtToken;

  @BeforeEach
  void setup() {
    // Register and login user
    var registerDto = new RegisterUserDto("giorgi", "tughushi", "giorgi@example.com", "password123");
    userService.registerUser(registerDto);

    dbClassroomId = classRoomRepository.findAll().stream()
        .findFirst()
        .orElseThrow(() -> new IllegalStateException("No classroom found"))
        .getId();

    jwtToken = userService.login(new LoginDto("giorgi@example.com", "password123")).getToken();
  }

  @Test
  void testGetCurrentActivities_returnsOkEvenIfEmpty() throws Exception {
    mockMvc.perform(get("/api/classroom/{classroomId}/activities", dbClassroomId)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk());
  }

  @Test
  void testCheckinUsingCurrentActivities() throws Exception {

    var activitiesResult = mockMvc.perform(get("/api/classroom/{classroomId}/activities", dbClassroomId)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn();

    var json = activitiesResult.getResponse().getContentAsString();
    CurrentActivitiesDto currentActivities = objectMapper.readValue(json, CurrentActivitiesDto.class);

    var activities = currentActivities.getActivities();
    if (activities.isEmpty()) {
      return;
    }

    String checkInToken = currentActivities.getCheckInToken();
    assertNotNull(checkInToken);
    assertFalse(activities.isEmpty());

    var activityId = activities.iterator().next().getId();
    var checkinDto = CheckinStudentDto.builder()
        .token(checkInToken)
        .activityId(activityId)
        .build();

    mockMvc.perform(post("/api/classroom/checkin")
            .header("Authorization", "Bearer " + jwtToken)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(checkinDto)))
        .andExpect(status().isOk());
  }
}

