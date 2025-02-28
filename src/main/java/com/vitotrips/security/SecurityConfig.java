package com.vitotrips.security;

import com.vitotrips.security.jwt.JwtAuthenticationEntryPoint;
import com.vitotrips.security.jwt.JwtAuthenticationFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UserDetailsRepositoryReactiveAuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;
    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    private final ReactiveUserDetailsService userDetailsService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter,
                          JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
                          ReactiveUserDetailsService userDetailsService) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
        this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                .exceptionHandling(exceptionHandlingSpec -> 
                    exceptionHandlingSpec.authenticationEntryPoint(
                        (exchange, ex) -> Mono.fromCallable(() -> {
                            jwtAuthenticationEntryPoint.commence(exchange.getRequest(), exchange.getResponse(), ex);
                            return null;
                        })
                    )
                )
                .authorizeExchange(exchanges -> exchanges
                        // Public API endpoints - new routes with /api/v1/ prefix
                        .pathMatchers("/api/v1/auth/login").permitAll()
                        .pathMatchers("/api/v1/auth/register").permitAll()
                        .pathMatchers(HttpMethod.GET.name(), "/api/v1/users/email/{email}").permitAll()
                        .pathMatchers(HttpMethod.GET.name(), "/api/v1/tours").permitAll()
                        .pathMatchers(HttpMethod.GET.name(), "/api/v1/tours/{id}").permitAll()
                        
                        // Block old API endpoints to ensure they're not accessible
                        .pathMatchers("/auth/**").denyAll()
                        .pathMatchers("/api/users/**").denyAll()
                        .pathMatchers("/api/tours/**").denyAll()
                        .pathMatchers("/api/bookings/**").denyAll()
                        .pathMatchers("/api/groups/**").denyAll()
                        .pathMatchers("/api/reviews/**").denyAll()
                        .pathMatchers("/api/volunteer-opportunities/**").denyAll()
                        .pathMatchers("/api/payments/**").denyAll()
                        
                        // Frontend routes - allow all Vue.js routes
                        .pathMatchers("/", "/index.html", "/login", "/register", "/tours", "/tours/**", "/home").permitAll()
                        
                        // Static resources
                        .pathMatchers("/js/**", "/css/**", "/img/**", "/favicon.ico").permitAll()
                        .pathMatchers("/static/**", "/resources/**").permitAll()
                        .pathMatchers("/templates/**").permitAll()
                        
                        // Swagger UI
                        .pathMatchers(
                                "/v3/api-docs",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/swagger-ui.html"
                        ).permitAll()

                        // Admin only endpoints
                        .pathMatchers(HttpMethod.POST.name(), "/api/v1/users").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.GET.name(), "/api/v1/users/role/**").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE.name(), "/api/v1/users/**").hasRole("ADMIN")

                        // Tour Operator and Admin endpoints
                        .pathMatchers(HttpMethod.GET.name(), "/api/v1/users").hasAnyRole("ADMIN", "TOUR_OPERATOR")

                        // Accessible by all authenticated users (Traveler, Tour Operator, Admin)
                        .pathMatchers(HttpMethod.GET.name(), "/api/v1/users/{id}").hasAnyRole("ADMIN", "TOUR_OPERATOR", "TRAVELER")

                        // API endpoints that require authentication
                        .pathMatchers("/api/v1/**").authenticated()
                        
                        // Allow all other requests (for frontend routing)
                        .anyExchange().permitAll()
                )
                .addFilterAt(jwtAuthenticationFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public ReactiveAuthenticationManager reactiveAuthenticationManager() {
        UserDetailsRepositoryReactiveAuthenticationManager authenticationManager = 
            new UserDetailsRepositoryReactiveAuthenticationManager(userDetailsService);
        authenticationManager.setPasswordEncoder(passwordEncoder());
        return authenticationManager;
    }
}