package com.asterbit.qrscanner.user;

import com.asterbit.qrscanner.checkins.CheckIn;
import jakarta.persistence.*;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToMany(mappedBy = "user")
    private Set<CheckIn> checkins = new HashSet<>();

}
