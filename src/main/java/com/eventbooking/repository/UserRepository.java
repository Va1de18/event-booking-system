package com.eventbooking.repository;

import com.eventbooking.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Репозиторий для работы с пользователями.
// findByEmail используется при логине и загрузке пользователя из JWT токена
    public interface UserRepository extends JpaRepository<User, Long>{
        Optional<User> findByEmail(String email);
        boolean existsByEmail(String email);
    }

