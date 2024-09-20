package com.jethjoe.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity // the class in an entity and it gets mapped to a database table
@Data
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private UserRole role;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @JsonIgnore // Prevents the bookings field from being serialized to JSON, when fetching user obj I don't need this list
    @OneToMany // Specifies a one-to-many relationship between User and Booking entities
    private List <Booking> bookings = new ArrayList<>();
}
