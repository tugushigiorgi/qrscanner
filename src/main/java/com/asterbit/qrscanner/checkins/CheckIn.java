package com.asterbit.qrscanner.checkins;

import com.asterbit.qrscanner.activity.Activity;
import com.asterbit.qrscanner.user.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "check_ins")
public class CheckIn {

  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "activity_id", nullable = false)
  private Activity activity;

  private LocalDateTime checkInDate;

  @ManyToOne
  @JsonIgnore
  @JoinColumn(name = "user_id", nullable = false)
  private User user;
}
