package com.vitotrips.DTO;

import com.vitotrips.model.UserRole;
import lombok.Getter;
import lombok.Setter;

// Request DTO for registration
@Setter
@Getter
public class RegistrationRequest {
    // Getters and setters
    private String email;
    private String password;
    private String name;
    private UserRole role;

}
