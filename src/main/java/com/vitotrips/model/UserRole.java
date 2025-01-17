package com.vitotrips.model;

import org.springframework.security.core.GrantedAuthority;

public enum UserRole implements GrantedAuthority {
    ROLE_TRAVELER,
    ROLE_TOUR_OPERATOR,
    ROLE_ADMIN;

    @Override
    public String getAuthority() {
        return this.name();  // No need to add "ROLE_" prefix since it's already in the enum
    }

    // Helper method to get role name without prefix if needed
    public String getRoleWithoutPrefix() {
        return this.name().substring(5);  // Remove "ROLE_" prefix
    }
}