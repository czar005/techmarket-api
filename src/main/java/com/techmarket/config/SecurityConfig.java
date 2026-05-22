package com.techmarket.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

  private final JwtAuthenticationFilter jwtAuthenticationFilter;

  public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
    this.jwtAuthenticationFilter = jwtAuthenticationFilter;
  }

  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth
                    // ========== ДОБАВЛЕНО: Swagger UI и OpenAPI ==========
                    .requestMatchers(
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/v3/api-docs/**",
                        "/v3/api-docs",
                        "/swagger-resources/**",
                        "/swagger-resources",
                        "/webjars/**",
                        "/api-docs/**")
                    .permitAll()
                    // ===================================================

                    // Публичные эндпоинты
                    .requestMatchers("/", "/api/auth/**")
                    .permitAll()
                    .requestMatchers("/api/listings")
                    .permitAll()
                    .requestMatchers("/api/listings/{id}")
                    .permitAll()
                    .requestMatchers("/api/listings/brand/**")
                    .permitAll()
                    .requestMatchers("/api/listings/condition/**")
                    .permitAll()
                    .requestMatchers("/api/listings/price-range")
                    .permitAll()
                    .requestMatchers("/api/listings/filter")
                    .permitAll()
                    .requestMatchers("/actuator/**")
                    .permitAll()

                    // Защищенные эндпоинты
                    .requestMatchers("/api/deals/**")
                    .authenticated()
                    .anyRequest()
                    .authenticated())
        .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }

  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
