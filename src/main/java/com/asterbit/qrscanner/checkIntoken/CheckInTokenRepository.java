package com.asterbit.qrscanner.checkIntoken;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInTokenRepository extends CrudRepository<CheckInToken, String> {

}
