package com.eventbooking.service;

import com.eventbooking.domain.*;
import com.eventbooking.dto.request.BookingRequest;
import com.eventbooking.dto.response.BookingResponse;
import com.eventbooking.exception.BookingConflictException;
import com.eventbooking.exception.NoSeatsAvailableException;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.BookingRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

// Тесты бизнес-логики бронирования через Mockito — без поднятия Spring контекста.
@ExtendWith(MockitoExtension.class)
class BookingServiceTest {

    @Mock private BookingRepository bookingRepository;
    @Mock private EventRepository eventRepository;
    @Mock private UserRepository userRepository;

    @InjectMocks
    private BookingService bookingService;

    private User user;
    private Event event;

    @BeforeEach
    void setUp() {
        // Мок SecurityContext — имитируем авторизованного пользователя
        Authentication auth = mock(Authentication.class);
        when(auth.getName()).thenReturn("user@mail.com");
        SecurityContext ctx = mock(SecurityContext.class);
        when(ctx.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(ctx);

        user = User.builder()
                .id(1L)
                .email("user@mail.com")
                .password("encoded")
                .fullName("Test User")
                .role(Role.USER)
                .build();

        Venue venue = Venue.builder()
                .id(1L).name("Stadium").address("St 1").city("Kyiv").capacity(100)
                .build();

        event = Event.builder()
                .id(1L)
                .title("Rock Fest")
                .eventDate(LocalDateTime.now().plusDays(10))
                .price(BigDecimal.valueOf(50))
                .availableSeats(10)
                .venue(venue)
                .category(Category.builder().id(1L).name("Concert").build())
                .build();
    }

    // Успешное бронирование — места списываются
    @Test
    void create_shouldCreateBooking_whenSeatsAvailable() {
        BookingRequest request = new BookingRequest();
        request.setEventId(1L);
        request.setSeatsCount(3);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(eventRepository.findByIdWithLock(1L)).thenReturn(Optional.of(event));
        when(bookingRepository.save(any())).thenAnswer(inv -> {
            Booking b = inv.getArgument(0);
            b = Booking.builder()
                    .id(1L).user(user).event(event)
                    .seatsCount(b.getSeatsCount()).status(BookingStatus.CONFIRMED)
                    .build();
            return b;
        });

        BookingResponse response = bookingService.create(request);

        assertThat(response.getStatus()).isEqualTo("CONFIRMED");
        assertThat(response.getSeatsCount()).isEqualTo(3);
        // Проверяем что места действительно уменьшились
        assertThat(event.getAvailableSeats()).isEqualTo(7);
        verify(eventRepository).save(event);
    }

    // Нет свободных мест — должно бросить исключение
    @Test
    void create_shouldThrow_whenNotEnoughSeats() {
        BookingRequest request = new BookingRequest();
        request.setEventId(1L);
        request.setSeatsCount(20); // больше чем availableSeats=10

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(eventRepository.findByIdWithLock(1L)).thenReturn(Optional.of(event));

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(NoSeatsAvailableException.class)
                .hasMessageContaining("Not enough seats");

        verify(bookingRepository, never()).save(any());
    }

    // Событие не найдено
    @Test
    void create_shouldThrow_whenEventNotFound() {
        BookingRequest request = new BookingRequest();
        request.setEventId(99L);
        request.setSeatsCount(1);

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(eventRepository.findByIdWithLock(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingService.create(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    // Отмена брони — места возвращаются
    @Test
    void cancel_shouldCancelBooking_andReturnSeats() {
        Booking booking = Booking.builder()
                .id(1L).user(user).event(event)
                .seatsCount(3).status(BookingStatus.CONFIRMED)
                .build();

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(booking));

        bookingService.cancel(1L);

        assertThat(booking.getStatus()).isEqualTo(BookingStatus.CANCELLED);
        assertThat(event.getAvailableSeats()).isEqualTo(13); // 10 + 3 возвращено
        verify(bookingRepository).save(booking);
    }

    // Нельзя отменить уже отменённую бронь
    @Test
    void cancel_shouldThrow_whenAlreadyCancelled() {
        Booking booking = Booking.builder()
                .id(1L).user(user).event(event)
                .seatsCount(3).status(BookingStatus.CANCELLED)
                .build();

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> bookingService.cancel(1L))
                .isInstanceOf(BookingConflictException.class)
                .hasMessageContaining("already cancelled");
    }

    // Получение броней текущего пользователя
    @Test
    void getMyBookings_shouldReturnUserBookings() {
        Booking booking = Booking.builder()
                .id(1L).user(user).event(event)
                .seatsCount(2).status(BookingStatus.CONFIRMED)
                .build();

        when(userRepository.findByEmail("user@mail.com")).thenReturn(Optional.of(user));
        when(bookingRepository.findByUserId(1L)).thenReturn(List.of(booking));

        List<BookingResponse> result = bookingService.getMyBookings();

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getEventTitle()).isEqualTo("Rock Fest");
    }
}