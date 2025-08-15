package com.asterbit.qrscanner.user.service.impl;

import com.asterbit.qrscanner.user.service.UserService;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

public class UserServiceImpl implements UserService {



//    @Override
//    public UUID currentUserId() {
//        if (authentication.getName() != null) {
//            var user = userRepository.findByEmail(authentication.getName());
//            if (user.isPresent()) {
//                log.info("User found with User ID: {}", user.get().getId());
//                return user.get().getId();
//            } else {
//                log.warn("No user found for email: {}", authentication.getName());
//            }
//        }
//
//        log.error("Failed to retrieve user ID: Authentication name is null or user not found.");
//        throw new ResponseStatusException(BAD_REQUEST, "User not found or invalid authentication");
//    }
}
