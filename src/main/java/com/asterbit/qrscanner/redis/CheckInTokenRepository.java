package com.asterbit.qrscanner.redis;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInTokenRepository extends CrudRepository<CheckInToken, String> {
 }
