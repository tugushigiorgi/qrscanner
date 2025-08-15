package com.asterbit.qrscanner.checkIntoken;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.io.Serializable;
import java.util.UUID;

@RedisHash(value =  "checkInToken", timeToLive = 300)
@Builder
@Getter
@Setter
public class CheckInToken implements Serializable {

    @Id
    private String id;
    private UUID token;

    @Indexed
    private UUID userId;
    private UUID classroomId;

}
