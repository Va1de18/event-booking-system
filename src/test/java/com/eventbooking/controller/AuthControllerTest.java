package com.eventbooking.controller;

import com.eventbooking.config.SecurityConfig;
import com.eventbooking.dto.request.LoginRequest;
import com.eventbooking.dto.request.RegisterRequest;
import com.eventbooking.dto.response.AuthResponse;
import com.eventbooking.service.AuthService;
import com.eventbooking.security.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// HTTP тесты через MockMvc — грузится только веб-слой, без БД и Flyway.
@WebMvcTest(controllers = AuthController.class)
@Import(SecurityConfig.class)
@SuppressWarnings("removal")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AuthService authService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    // Успешная регистрация — возвращает 200 и токен
    @Test
    void register_shouldReturn200_whenValidRequest() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");
        request.setFullName("Test User");

        AuthResponse response = new AuthResponse("jwt-token", "test@mail.com", "USER");
        when(authService.register(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").value("jwt-token"))
                .andExpect(jsonPath("$.email").value("test@mail.com"))
                .andExpect(jsonPath("$.role").value("USER"));
    }

    // Невалидный email — возвращает 400
    @Test
    void register_shouldReturn400_whenInvalidEmail() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("not-an-email");
        request.setPassword("123456");
        request.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // Пустой пароль — возвращает 400
    @Test
    void register_shouldReturn400_whenPasswordTooShort() throws Exception {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123"); // меньше 6 символов
        request.setFullName("Test User");

        mockMvc.perform(post("/api/auth/register").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // Успешный логин — возвращает токен
    @Test
    void login_shouldReturn200_whenValidCredentials() throws Exception {
        LoginRequest request = new LoginRequest();
        request.setEmail("test@mail.com");
        request.setPassword("123456");

        AuthResponse response = new AuthResponse("jwt-token", "test@mail.com", "USER");
        when(authService.login(any())).thenReturn(response);

        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").exists());
    }

    // Пустое тело запроса — возвращает 400
    @Test
    void login_shouldReturn400_whenEmptyBody() throws Exception {
        mockMvc.perform(post("/api/auth/login").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isBadRequest());
    }
}
