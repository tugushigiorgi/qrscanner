package com.asterbit.qrscanner.checkIntoken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CheckInTokenRepository extends CrudRepository<CheckInToken, String> {
    Optional<CheckInToken> findByUserId(UUID userId);
}
