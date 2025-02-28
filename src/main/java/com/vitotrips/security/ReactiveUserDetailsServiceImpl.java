package com.vitotrips.security;

import com.vitotrips.model.User;
import com.vitotrips.service.UserService;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;

@Service
public class ReactiveUserDetailsServiceImpl implements ReactiveUserDetailsService {

    private final UserService userService;

    public ReactiveUserDetailsServiceImpl(UserService userService) {
        this.userService = userService;
    }

    @Override
    public Mono<UserDetails> findByUsername(String username) {
        try {
            User user = userService.getUserByEmail(username);
            
            // Create a UserDetails object from the User entity
            org.springframework.security.core.userdetails.User userDetails = 
                new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPasswordHash(),
                    Collections.singletonList(
                        new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
                    )
                );
            
            return Mono.just(userDetails);
        } catch (UsernameNotFoundException e) {
            return Mono.empty();
        } catch (Exception e) {
            return Mono.error(e);
        }
    }
} 