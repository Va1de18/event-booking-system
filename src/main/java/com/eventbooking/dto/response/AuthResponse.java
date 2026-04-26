package com.eventbooking.dto.response;

import lombok.AllArgsConstructor;
import lombok.Data;

// Ответ после успешного логина или регистрации.
// Клиент сохраняет token и отправляет его в заголовке Authorization: Bearer <token>.
@Data
@AllArgsConstructor
public class AuthResponse {
    private String token;
    private String email;
    private String role;
}
