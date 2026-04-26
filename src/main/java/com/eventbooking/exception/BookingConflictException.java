package com.eventbooking.exception;

// Бросается когда пользователь пытается отменить чужую бронь. Возвращает клиенту 403.
public class BookingConflictException extends RuntimeException {
    public BookingConflictException(String message) {
        super(message);
    }
}