package com.vitotrips.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.format.annotation.DateTimeFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * User entity mapped to the `users` database table.
 */
@Entity
@Table(name = "users") // Explicit table name for clarity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder

public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Primary key generation strategy
    private Long id;

    @Column(name = "name", nullable = false) // Add explicit column mapping with constraints
    @NotNull
    @Size(min = 1, max = 100)
    private String name;

    @Column(name = "email", unique = true, nullable = false) // Unique and non-nullable
    @NotNull
    @Email // Validate email format
    private String email;

    @Column(name = "password_hash", nullable = false) // Explicit column name and constraint
    @NotNull
    @JsonIgnore // Prevent serialization for sensitive data
    private String passwordHash;

    @Enumerated(EnumType.STRING) // Store role as a string in the database
    @Column(name = "role", nullable = false)
    @NotNull
    private UserRole role;

    @CreatedDate // Automatically set createdAt when the record is created
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) // Ensure proper ISO date format
    private LocalDateTime createdAt;

    @LastModifiedDate // Automatically update updatedAt when the record is modified
    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private LocalDateTime updatedAt;

    @JsonIgnore // Prevent fetching all bookings when serializing User
    @OneToMany(mappedBy = "user", fetch = FetchType.LAZY, cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Booking> bookings = new ArrayList<>();

    /**
     * Custom constructor for required fields.
     */
    public User(String name, String email, String passwordHash, UserRole role) {
        this.name = name;
        this.email = email;
        this.passwordHash = passwordHash;
        this.role = role;
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }

    // UserDetails interface implementation
    @Override
    @JsonIgnore
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.singleton(role);
    }

    @Override
    @JsonIgnore
    public String getPassword() {
        return this.passwordHash;
    }

    @Override
    @JsonIgnore
    public String getUsername() {
        return this.email;  // Using email as the username
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    @JsonIgnore
    public boolean isEnabled() {
        return true;
    }


    @Override
    public String toString() {
        return "User{id=%d, name='%s', email='%s', role='%s', createdAt=%s}".formatted(
                id, name, email, role, createdAt
        );
    }
}