package com.eventbooking.service;

import com.eventbooking.domain.Category;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Управляет категориями событий.
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<Category> getAll() {
        return categoryRepository.findAll();
    }

    public Category getById(Long id) {
        return categoryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + id));
    }

    public Category create(Category category) {
        return categoryRepository.save(category);
    }
}