package com.asterbit.qrscanner.integrationTests.classroom;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.asterbit.qrscanner.classroom.dto.CheckinStudentDto;
import com.asterbit.qrscanner.classroom.dto.CurrentActivitiesDto;
import com.asterbit.qrscanner.security.dto.LoginDto;
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

@SpringBootTest
@AutoConfigureMockMvc
public class ClassroomControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private UserService userService;

  @Autowired
  private ObjectMapper objectMapper;

  private UUID classroomId;
  private String jwtToken;

  @BeforeEach
  void setup() {
    // Register and login user
//    var registerDto = new RegisterUserDto("John", "Doe", "jjohn@example.com", "password123");
//    userService.registerUser(registerDto);

    jwtToken = userService.login(new LoginDto("jjohn@example.com", "password123")).getToken();
    classroomId = UUID.randomUUID();
  }

  @Test
  void testGetCurrentActivities() throws Exception {
    mockMvc.perform(get("/api/classroom/c1eab8ae-a7ab-40d8-a6da-931df645b94b/activities", classroomId)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.activities", notNullValue())); // adjust to your CurrentActivitiesDto fields
  }

  @Test
  void testCheckinUsingCurrentActivities() throws Exception {

    var activitiesResult = mockMvc.perform(get("/api/classroom/c1eab8ae-a7ab-40d8-a6da-931df645b94b/activities", classroomId)
            .header("Authorization", "Bearer " + jwtToken))
        .andExpect(status().isOk())
        .andReturn();

    var json = activitiesResult.getResponse().getContentAsString();
    CurrentActivitiesDto currentActivities = objectMapper.readValue(json, CurrentActivitiesDto.class);

    String checkInToken = currentActivities.getCheckInToken();
    var activities = currentActivities.getActivities();
    assertNotNull(checkInToken);
    assertNotNull(activities);
    assert (!activities.isEmpty());

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
