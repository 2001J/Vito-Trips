package com.vitotrips.controller;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import reactor.core.publisher.Mono;

/**
 * Controller for serving the Vue.js single-page application.
 * This controller handles all routes that should be handled by the frontend router.
 */
@Controller
public class WebController {

    /**
     * Forwards all routes to index.html so that the Vue.js router can handle them.
     * This allows for client-side routing with history mode.
     *
     * @return the index.html resource as a Mono
     */
    @GetMapping(value = {
            "/",
            "/login",
            "/register",
            "/tours",
            "/tours/**",
            "/booking/**",
            "/payment/**",
            "/profile",
            "/bookings",
            "/volunteer",
            "/admin",
            "/operator",
            "/about",
            "/contact",
            "/forgot-password"
    }, produces = MediaType.TEXT_HTML_VALUE)
    @ResponseBody
    public Mono<Resource> forward() {
        return Mono.just(new ClassPathResource("templates/index.html"));
    }
} 