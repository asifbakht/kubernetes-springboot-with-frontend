package com.microservice.customer.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
@EnableWebMvc
public class CorsConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry
                .addMapping("/**")
                .allowedOrigins("*")
                .allowedMethods(
                        HttpMethod.PUT.name(),
                        HttpMethod.POST.name(),
                        HttpMethod.GET.name(),
                        HttpMethod.DELETE.name(),
                        HttpMethod.OPTIONS.name()
                        )
        ;

    }
}