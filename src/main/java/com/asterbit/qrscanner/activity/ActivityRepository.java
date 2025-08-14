package com.asterbit.qrscanner.activity;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Repository
public interface ActivityRepository extends JpaRepository<Activity, UUID> {
    @Query("""
                SELECT a FROM Activity a
                WHERE a.classroom.id = :classroomId
                AND a.startTime BETWEEN :fromTime AND :toTime
            """)
    Set<Activity> findActivitiesStartingInRange(
            @Param("classroomId") UUID classroomId,
            @Param("fromTime") LocalDateTime fromTime,
            @Param("toTime") LocalDateTime toTime
    );
}
