package com.eventbooking.repository;

import com.eventbooking.domain.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

// Репозиторий для работы с категориями.
public interface CategoryRepository extends JpaRepository<Category, Long> {
    Optional<Category> findByName(String name);
    }
