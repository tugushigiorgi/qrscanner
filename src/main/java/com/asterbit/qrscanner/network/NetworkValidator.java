package com.asterbit.qrscanner.network;

import static org.springframework.util.CollectionUtils.isEmpty;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class NetworkValidator {

  private final SchoolNetworkProperties schoolNetworkProperties;

  public boolean isFromSchool(HttpServletRequest request) {
    var clientIp = getClientIp(request);
    if (isEmpty(schoolNetworkProperties.getAllowedPublicIps())) {
      return true;
    }
    return schoolNetworkProperties.getAllowedPublicIps().contains(clientIp);
  }

  private String getClientIp(HttpServletRequest request) {
    var xfHeader = request.getHeader("X-Forwarded-For");
    if (xfHeader != null && !xfHeader.isEmpty()) {
      return xfHeader.split(",")[0].trim();
    }
    return request.getRemoteAddr();
  }
}
