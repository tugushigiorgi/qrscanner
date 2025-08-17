package com.asterbit.qrscanner.exceptions;

import static com.asterbit.qrscanner.util.ConstMessages.USER_NOT_FOUND;

import java.util.UUID;

public class UserNotFoundException extends RuntimeException {
  private final UUID userId;

  public UserNotFoundException(UUID userId) {
    super(USER_NOT_FOUND + userId);
    this.userId = userId;
  }

  public UUID getUserId() {
    return userId;
  }
}
