package com.asterbit.qrscanner.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RegisterUserDto {

  @NotBlank(message = "Name is required")
  public String name;

  @NotBlank(message = "Surname is required")
  public String surname;

  @NotBlank(message = "Email is required")
  @Email(message = "invalid email format")
  public String email;

  @NotNull
  @Size(min = 6, message = "password must be at least 6 characters long")
  public String password;
}