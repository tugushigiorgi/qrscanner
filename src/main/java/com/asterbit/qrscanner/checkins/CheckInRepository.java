package com.asterbit.qrscanner.checkins;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CheckInRepository extends JpaRepository<CheckIn, UUID> {

  @Query("""
      SELECT COUNT(c) > 0
      FROM CheckIn c
      WHERE c.user.id = :userId
        AND c.activity.id = :activityId
      """)
  boolean existsByUserAndActivity(@Param("userId") UUID userId,
                                  @Param("activityId") UUID activityId);

}
