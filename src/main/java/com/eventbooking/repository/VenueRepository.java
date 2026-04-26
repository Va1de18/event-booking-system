package com.eventbooking.repository;

import com.eventbooking.domain.Venue;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

// Репозиторий для работы с площадками.
public interface VenueRepository extends JpaRepository<Venue, Long> {
    List<Venue> findByCity(String city);
}
