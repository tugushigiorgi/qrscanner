package com.asterbit.qrscanner.integrationTests.security;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.asterbit.qrscanner.security.dto.LoginDto;
import com.asterbit.qrscanner.user.dto.RegisterUserDto;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
public class AuthControllerIntegrationTest {

  @Autowired
  private MockMvc mockMvc;

  @Autowired
  private ObjectMapper objectMapper;

  @Test
  void testRegisterUser() throws Exception {
    var registerDto = RegisterUserDto.builder()
        .name("Giorgi")
        .surname("Tughushi")
        .email("new2@example.com")
        .password("password123")
        .build();

    var result = mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.firstName").value("Giorgi"))
        .andExpect(jsonPath("$.lastName").value("Tughushi"))
        .andExpect(jsonPath("$.email").value("new2@example.com"))
        .andReturn();
  }

  @Test
  void testLoginUser() throws Exception {
    // register the user
    var registerDto = RegisterUserDto.builder()
        .name("giorgi")
        .surname("tughushi")
        .email("gio@example.com")
        .password("password123")
        .build();

    mockMvc.perform(post("/api/auth/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerDto)))
        .andExpect(status().isOk());

    // Login
    var loginDto = LoginDto.builder()
        .email("gio@example.com")
        .password("password123")
        .build();

    var result = mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(loginDto)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.token", notNullValue()))
        .andReturn();

    var response = result.getResponse().getContentAsString();
    assertNotNull(response);
  }
}
