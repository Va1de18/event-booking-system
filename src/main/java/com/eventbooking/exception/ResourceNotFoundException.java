package com.eventbooking.exception;

// Бросается когда сущность не найдена в БД. Возвращает клиенту 404.
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}