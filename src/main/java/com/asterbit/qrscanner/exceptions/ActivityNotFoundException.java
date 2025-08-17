package com.asterbit.qrscanner.exceptions;

import static com.asterbit.qrscanner.util.ConstMessages.ACTIVITY_NOT_FOUND;

import java.util.UUID;

public class ActivityNotFoundException extends RuntimeException {
  private final UUID activityId;

  public ActivityNotFoundException(UUID activityId) {
    super(String.format(ACTIVITY_NOT_FOUND, activityId));
    this.activityId = activityId;
  }

  public UUID getActivityId() {
    return activityId;
  }
}
