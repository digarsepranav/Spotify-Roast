package com.spotify.demo;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain (HttpSecurity httpSecurity) {
    return httpSecurity.authorizeHttpRequests(
            auth -> auth.requestMatchers("/api/roast", "/api/ai/roast").permitAll()
                    .requestMatchers("/api/ai/history").authenticated()
                    .anyRequest().authenticated()
    ).build();
    }
}
