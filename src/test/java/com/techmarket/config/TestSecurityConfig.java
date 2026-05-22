package com.techmarket.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
public class TestSecurityConfig {

  @Bean
  @Primary
  public SecurityFilterChain testSecurityFilterChain(HttpSecurity http) throws Exception {
    http.csrf(csrf -> csrf.disable())
        .sessionManagement(
            session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(
            auth ->
                auth.requestMatchers("/api/listings/add")
                    .authenticated()
                    .requestMatchers("/api/listings/user")
                    .authenticated()
                    .requestMatchers("/api/listings/*")
                    .authenticated()
                    .anyRequest()
                    .permitAll());

    return http.build();
  }

  @Bean
  @Primary
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }
}
