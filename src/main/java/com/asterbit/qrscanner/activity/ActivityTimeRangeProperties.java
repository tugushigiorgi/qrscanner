package com.asterbit.qrscanner.activity;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "activity.time-range")
public class ActivityTimeRangeProperties {
    private long startOffsetMinutes;
    private long endOffsetHours;
}