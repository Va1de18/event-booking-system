package com.eventbooking.exception;

// Бросается когда свободных мест недостаточно для бронирования. Возвращает клиенту 409.
public class NoSeatsAvailableException extends RuntimeException {
    public NoSeatsAvailableException(String message) {
        super(message);
    }
}