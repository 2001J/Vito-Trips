package com.vitotrips.security.jwt;

import com.vitotrips.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
public class JwtAuthenticationFilter implements WebFilter {

    private final JwtProvider jwtProvider;
    private final ReactiveUserDetailsService userDetailsService;

    public JwtAuthenticationFilter(JwtProvider jwtProvider, ReactiveUserDetailsService userDetailsService) {
        this.jwtProvider = jwtProvider;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        ServerHttpRequest request = exchange.getRequest();
        
        // Skip authentication for login endpoint
        if (request.getPath().value().equals("/api/v1/auth/login")) {
            return chain.filter(exchange);
        }
        
        String token = extractToken(request);
        
        if (token != null && jwtProvider.validateToken(token)) {
            String userEmail = jwtProvider.extractEmail(token);
            
            return userDetailsService.findByUsername(userEmail)
                .map(userDetails -> {
                    UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    return authentication;
                })
                .flatMap(authentication -> 
                    chain.filter(exchange)
                        .contextWrite(ReactiveSecurityContextHolder.withAuthentication(authentication))
                );
        }
        
        return chain.filter(exchange);
    }

    private String extractToken(ServerHttpRequest request) {
        String authHeader = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7); // Remove "Bearer " prefix
        }
        return null;
    }
}