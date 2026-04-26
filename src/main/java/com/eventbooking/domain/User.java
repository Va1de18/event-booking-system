package com.eventbooking.domain;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

// Пользователь системы. Хранит учётные данные и роль.
// Пароль хранится в зашифрованном виде (bcrypt), email уникален.
@Entity
@Table(name= "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String email;

    @Column(nullable = false)
    private String password;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    // Роль хранится в БД как строка (USER/ADMIN), не как число
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    // Автоматически проставляется дата создания перед первым сохранением в БД
    @PrePersist
    protected void onCreate(){
        createdAt = LocalDateTime.now();
    }
}
