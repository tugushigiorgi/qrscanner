package com.asterbit.qrscanner.network;

import java.util.List;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "school.network")
public class SchoolNetworkProperties {
  private List<String> allowedPublicIps;
}
