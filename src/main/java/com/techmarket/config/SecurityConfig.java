package com.techmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/api/auth/**").permitAll()  // Разрешаем корневой и auth эндпоинты
                        .requestMatchers("/api/listings").permitAll()  // Разрешаем просмотр объявлений без аутентификации
                        .requestMatchers("/api/listings/{id}").permitAll()  // Разрешаем просмотр конкретного объявления
                        .requestMatchers("/api/listings/brand/**").permitAll()  // Разрешаем фильтрацию по бренду
                        .requestMatchers("/api/listings/condition/**").permitAll()  // Разрешаем фильтрацию по состоянию
                        .requestMatchers("/api/listings/price-range").permitAll()  // Разрешаем фильтрацию по цене
                        .requestMatchers("/api/listings/filter").permitAll()  // Разрешаем фильтрацию
                        .anyRequest().authenticated()
                )
                .addFilterBefore(jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}