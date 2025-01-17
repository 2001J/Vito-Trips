package com.vitotrips.repository;

import com.vitotrips.model.User;
import com.vitotrips.model.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

    // Fetch user by email
    Optional<User> findByEmail(String email);

    // Fetch users by role (now uses UserRole enum)
    List<User> findByRole(UserRole role);
}