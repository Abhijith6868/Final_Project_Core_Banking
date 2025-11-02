package com.corebank.coreb.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.corebank.coreb.service.StaffUserService;

@Configuration

public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthFilter;
    private final StaffUserService staffUserService;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, StaffUserService staffUserService) {
        this.jwtAuthFilter = jwtAuthFilter;
        this.staffUserService = staffUserService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                	    .requestMatchers("/api/auth/login", "/api/staff/create").permitAll()
                	    .requestMatchers("/branches/**").permitAll()
                	    .requestMatchers("/api/admin/**").hasRole("ADMIN")
                	    .requestMatchers("/api/staff/request").hasAnyRole("STAFF", "ADMIN")
                	    .requestMatchers("/api/staff/**").hasAnyRole("STAFF", "ADMIN")
                	    .anyRequest().authenticated()
                	)
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

 
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config)
            throws Exception {
        return config.getAuthenticationManager();
    }
}
