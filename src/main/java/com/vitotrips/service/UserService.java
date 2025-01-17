package com.vitotrips.service;

import com.vitotrips.model.User;
import com.vitotrips.model.UserRole;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.Optional;

public interface UserService extends UserDetailsService {
    User saveUser(User user);
    List<User> getUsersByRole(UserRole role);
    User getUserByEmail(String email) throws UsernameNotFoundException;
    Optional<User> getUserById(Long id);
    List<User> getAllUsers();
    void deleteUser(Long id);
    boolean existsByEmail(String email);
}