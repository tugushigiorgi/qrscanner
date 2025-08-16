package com.asterbit.qrscanner.activity;

import com.asterbit.qrscanner.checkins.CheckIn;
import com.asterbit.qrscanner.classroom.Classroom;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Builder
@Getter
@Setter
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

  @OneToMany(mappedBy = "activity", cascade = CascadeType.ALL, orphanRemoval = true)
  @JsonIgnore
  @ToString.Exclude
  @EqualsAndHashCode.Exclude
  private Set<CheckIn> checkIns = new HashSet<>();

  public void addCheckIn(CheckIn checkIn) {
    checkIns.add(checkIn);
    checkIn.setActivity(this);
  }
}
