package com.vitotrips.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.config.EnableWebFlux;
import org.springframework.web.reactive.config.ResourceHandlerRegistry;
import org.springframework.web.reactive.config.WebFluxConfigurer;
import org.springframework.web.reactive.resource.PathResourceResolver;

/**
 * WebFlux configuration for serving static resources
 */
@Configuration
@EnableWebFlux
public class WebConfig implements WebFluxConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Register resource handlers for static resources
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/static/", "classpath:/templates/")
                .resourceChain(true)
                .addResolver(new PathResourceResolver());
                
        // Specific handlers for common static resource paths
        registry.addResourceHandler("/js/**")
                .addResourceLocations("classpath:/static/js/");
                
        registry.addResourceHandler("/img/**")
                .addResourceLocations("classpath:/static/img/");
                
        registry.addResourceHandler("/css/**")
                .addResourceLocations("classpath:/static/css/");
                
        // Handler for index.html
        registry.addResourceHandler("/", "/index.html")
                .addResourceLocations("classpath:/templates/");
    }
} 