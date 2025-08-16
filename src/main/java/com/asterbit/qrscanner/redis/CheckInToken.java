package com.asterbit.qrscanner.redis;

import jakarta.persistence.Id;
import java.io.Serializable;
import java.util.UUID;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;

@RedisHash(value = "checkInToken", timeToLive = 300)
@Builder
@Getter
@Setter
public class CheckInToken implements Serializable {

  @Id
  private String id;
  private UUID userId;
  private UUID classroomId;

}
