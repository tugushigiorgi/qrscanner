package com.asterbit.qrscanner.exceptions;

import static com.asterbit.qrscanner.util.ConstMessages.USER_ALREADY_CHECKED_IN;

import java.util.UUID;

public class AlreadyCheckedInException extends RuntimeException {
  private final UUID activityId;

  public AlreadyCheckedInException(UUID activityId) {
    super(USER_ALREADY_CHECKED_IN + activityId);
    this.activityId = activityId;
  }

  public UUID getActivityId() {
    return activityId;
  }
}
