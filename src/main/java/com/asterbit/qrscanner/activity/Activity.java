package com.asterbit.qrscanner.activity;

import com.asterbit.qrscanner.checkins.CheckIn;
import com.asterbit.qrscanner.classroom.Classroom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "activities")
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;

    private LocalDateTime startTime;

    private LocalDateTime endTime;

    private String description;

    @ManyToOne
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    @JoinColumn(name = "classroom_id", nullable = false)
    private Classroom classroom;

    @OneToMany(mappedBy = "activity")
    @JsonIgnore
    @ToString.Exclude
    @EqualsAndHashCode.Exclude
    private Set<CheckIn> checkIns = new HashSet<>();
}
