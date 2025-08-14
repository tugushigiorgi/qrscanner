package com.asterbit.qrscanner.checkIntoken;

import jakarta.persistence.Id;
import lombok.Builder;
import org.springframework.data.redis.core.RedisHash;

import java.io.Serializable;
import java.util.UUID;

@RedisHash("checkInToken")
@Builder
public class CheckInToken implements Serializable {

    @Id
    private String id;
    private UUID token;
    private UUID userId;
    private UUID classroomId;

}
