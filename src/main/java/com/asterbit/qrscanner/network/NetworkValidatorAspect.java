package com.asterbit.qrscanner.network;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import static com.asterbit.qrscanner.util.ConstMessages.OUTSIDE_SCHOOL_NETWORK;

@Aspect
@Component
@RequiredArgsConstructor
public class NetworkValidatorAspect {

    private final HttpServletRequest request;
    private final NetworkValidator networkValidator;

    @Before("@annotation(RequireSchoolWifi) || @within(RequireSchoolWifi)")
    public void checkSchoolWifi() {
        if (!networkValidator.isFromSchool(request)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN,
                    OUTSIDE_SCHOOL_NETWORK);
        }
    }
}