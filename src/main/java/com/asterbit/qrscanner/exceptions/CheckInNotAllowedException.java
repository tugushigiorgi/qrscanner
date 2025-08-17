package com.asterbit.qrscanner.exceptions;

import java.time.LocalDateTime;

public class CheckInNotAllowedException extends RuntimeException {
  private final LocalDateTime latestAllowed;

  public CheckInNotAllowedException(LocalDateTime latestAllowed) {
    super("Check-in not allowed after: " + latestAllowed);
    this.latestAllowed = latestAllowed;
  }

  public LocalDateTime getLatestAllowed() {
    return latestAllowed;
  }
}

