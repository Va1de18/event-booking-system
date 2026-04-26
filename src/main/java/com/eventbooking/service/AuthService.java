package com.eventbooking.service;

import com.eventbooking.domain.Role;
import com.eventbooking.domain.User;
import com.eventbooking.dto.request.LoginRequest;
import com.eventbooking.dto.request.RegisterRequest;
import com.eventbooking.dto.response.AuthResponse;
import com.eventbooking.exception.BookingConflictException;
import com.eventbooking.repository.UserRepository;
import com.eventbooking.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

// Отвечает за регистрацию и логин пользователей.
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;

    // Регистрирует нового пользователя. Проверяет уникальность email.
    public AuthResponse register(RegisterRequest request){
        if (userRepository.existsByEmail(request.getEmail())){
            throw new BookingConflictException("Email already in use: " + request.getEmail());
        }

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode((request.getPassword())))
                .fullName(request.getFullName())
                .role(Role.USER)
                .build();

        userRepository.save(user);

        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }

    // Проверяет логин пароль через Spring Security и возвращает JWT токен.
    public AuthResponse login(LoginRequest request){
        // Бросит исключение если email или пароль неверны
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        User user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String token = jwtTokenProvider.generateToken(user.getEmail());
        return new AuthResponse(token, user.getEmail(), user.getRole().name());
    }
}
