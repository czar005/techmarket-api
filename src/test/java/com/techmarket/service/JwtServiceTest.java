package com.techmarket.service;

import static org.junit.jupiter.api.Assertions.*;

import com.techmarket.model.User;
import io.jsonwebtoken.security.Keys;
import java.lang.reflect.Field;
import java.security.Key;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class JwtServiceTest {

  private JwtService jwtService;

  @BeforeEach
  void setUp() {
    jwtService = new JwtService();
    try {
      Field secretField = JwtService.class.getDeclaredField("SECRET");
      secretField.setAccessible(true);
      Field keyField = JwtService.class.getDeclaredField("key");
      keyField.setAccessible(true);

      String secret = (String) secretField.get(jwtService);
      Key key = Keys.hmacShaKeyFor(secret.getBytes());
      keyField.set(jwtService, key);
    } catch (Exception e) {
      throw new RuntimeException(e);
    }
  }

  @Test
  void generateToken_NotNull() {
    User user = new User();
    user.setEmail("test@test.com");
    user.setRole("USER");

    String token = jwtService.generateToken(user);

    assertNotNull(token);
    assertFalse(token.isEmpty());
  }

  @Test
  void extractEmail_ValidToken() {
    User user = new User();
    user.setEmail("extract@test.com");
    user.setRole("USER");
    String token = jwtService.generateToken(user);

    String email = jwtService.extractEmail(token);

    assertEquals("extract@test.com", email);
  }
}
