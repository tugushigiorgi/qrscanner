package com.asterbit.qrscanner.user;

import com.asterbit.qrscanner.checkins.CheckIn;
import jakarta.persistence.*;
import lombok.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "user",cascade = CascadeType.ALL,orphanRemoval = true)
    private Set<CheckIn> checkins = new HashSet<>();

    public void addCheckIn(CheckIn checkIn) {
        checkins.add(checkIn);
        checkIn.setUser(this);
    }

}
