package com.asterbit.qrscanner.exceptions;

import static com.asterbit.qrscanner.util.ConstMessages.CLASSROOM_NOT_FOUND_WITH_ID;

import java.util.UUID;

public class ClassroomNotFoundException extends RuntimeException {
  private final UUID classroomId;

  public ClassroomNotFoundException(UUID classroomId) {
    super(String.format(CLASSROOM_NOT_FOUND_WITH_ID, classroomId));
    this.classroomId = classroomId;
  }

  public UUID getClassroomId() {
    return classroomId;
  }
}
