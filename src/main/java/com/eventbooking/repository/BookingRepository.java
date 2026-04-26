package com.eventbooking.repository;

import com.eventbooking.domain.Booking;
import com.eventbooking.domain.BookingStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

// Репозиторий для работы с бронями.
// Все запросы фильтруются по userId — пользователь видит только свои брони.
public interface BookingRepository extends JpaRepository<Booking, Long>{
    List<Booking> findByUserId(Long userId);
    List<Booking> findByEventId(Long eventId);
    Optional<Booking> findByIdAndUserId(Long id, Long userId);
    boolean existsByUserIdAndEventIdAndStatus(Long userId, Long eventId, BookingStatus status);
}
