package com.eventbooking.service;

import com.eventbooking.domain.Booking;
import com.eventbooking.domain.BookingStatus;
import com.eventbooking.domain.Event;
import com.eventbooking.domain.User;
import com.eventbooking.dto.request.BookingRequest;
import com.eventbooking.dto.response.BookingResponse;
import com.eventbooking.exception.BookingConflictException;
import com.eventbooking.exception.NoSeatsAvailableException;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

// Самый важный сервис — управляет бронированием.
// @Transactional гарантирует что проверка мест и сохранение брони — одна атомарная операция.
@Service
@RequiredArgsConstructor
public class BookingService {

    private final BookingRepository bookingRepository;
    private final EventRepository eventRepository;
    private final UserRepository userRepository;

    // Возвращает все брони текущего пользователя
    public List<BookingResponse> getMyBookings() {
        User user = getCurrentUser();
        return bookingRepository.findByUserId(user.getId()).stream()
                .map(this::toResponse)
                .toList();
    }

    // Создаёт бронь. Блокирует строку Event в БД чтобы два запроса не забронировали одно место.
    @Transactional
    public BookingResponse create(BookingRequest request) {
        User user = getCurrentUser();

        // PESSIMISTIC_WRITE — никто другой не может читать/писать эту строку пока транзакция не завершится
        Event event = eventRepository.findByIdWithLock(request.getEventId())
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + request.getEventId()));

        if (event.getAvailableSeats() < request.getSeatsCount()) {
            throw new NoSeatsAvailableException(
                    "Not enough seats. Available: " + event.getAvailableSeats());
        }

        // Уменьшаем количество мест и сохраняем бронь атомарно
        event.setAvailableSeats(event.getAvailableSeats() - request.getSeatsCount());
        eventRepository.save(event);

        Booking booking = Booking.builder()
                .user(user)
                .event(event)
                .seatsCount(request.getSeatsCount())
                .status(BookingStatus.CONFIRMED)
                .build();

        return toResponse(bookingRepository.save(booking));
    }

    // Отменяет бронь и возвращает места. Нельзя отменить чужую бронь.
    @Transactional
    public void cancel(Long bookingId) {
        User user = getCurrentUser();

        Booking booking = bookingRepository.findByIdAndUserId(bookingId, user.getId())
                .orElseThrow(() -> new BookingConflictException("Booking not found or does not belong to you"));

        if (booking.getStatus() == BookingStatus.CANCELLED) {
            throw new BookingConflictException("Booking is already cancelled");
        }

        // Возвращаем места событию
        Event event = booking.getEvent();
        event.setAvailableSeats(event.getAvailableSeats() + booking.getSeatsCount());
        eventRepository.save(event);

        booking.setStatus(BookingStatus.CANCELLED);
        bookingRepository.save(booking);
    }

    // Получает текущего авторизованного пользователя из SecurityContext
    private User getCurrentUser() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    }

    private BookingResponse toResponse(Booking booking) {
        return BookingResponse.builder()
                .id(booking.getId())
                .eventId(booking.getEvent().getId())
                .eventTitle(booking.getEvent().getTitle())
                .eventDate(booking.getEvent().getEventDate())
                .seatsCount(booking.getSeatsCount())
                .status(booking.getStatus().name())
                .createdAt(booking.getCreatedAt())
                .build();
    }
}