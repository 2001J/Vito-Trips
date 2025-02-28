package com.vitotrips.config;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

/**
 * API Gateway configuration for routing requests to appropriate microservices.
 * This configuration is only active when the "gateway" profile is active.
 */
@Configuration
@Profile("gateway")
public class ApiGatewayConfig {

    @Bean
    public RouteLocator customRouteLocator(RouteLocatorBuilder builder) {
        return builder.routes()
                // User Service Routes
                .route("user-service", r -> r.path("/api/v1/users/**", "/api/v1/auth/**")
                        .uri("lb://user-service"))
                
                // Tour Service Routes
                .route("tour-service", r -> r.path("/api/v1/tours/**", "/api/v1/locations/**")
                        .uri("lb://tour-service"))
                
                // Booking Service Routes
                .route("booking-service", r -> r.path("/api/v1/bookings/**", "/api/v1/payments/**")
                        .uri("lb://booking-service"))
                
                // Volunteer Service Routes
                .route("volunteer-service", r -> r.path("/api/v1/volunteer/**", "/api/v1/impact/**")
                        .uri("lb://volunteer-service"))
                
                // Review Service Routes
                .route("review-service", r -> r.path("/api/v1/reviews/**")
                        .uri("lb://review-service"))
                
                // Group Service Routes
                .route("group-service", r -> r.path("/api/v1/groups/**", "/api/v1/group-members/**")
                        .uri("lb://group-service"))
                
                // Fallback route for static resources
                .route("static-resources", r -> r.path("/static/**")
                        .uri("lb://resource-service"))
                
                .build();
    }
} 