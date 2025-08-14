package com.asterbit.qrscanner.network;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Data
@Component
@ConfigurationProperties(prefix = "school.network")
public class SchoolNetworkProperties {
    private List<String> allowedPublicIps;;
}
