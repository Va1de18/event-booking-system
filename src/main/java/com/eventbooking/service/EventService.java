package com.eventbooking.service;

import com.eventbooking.domain.Category;
import com.eventbooking.domain.Event;
import com.eventbooking.domain.Venue;
import com.eventbooking.dto.request.EventRequest;
import com.eventbooking.dto.response.EventResponse;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.CategoryRepository;
import com.eventbooking.repository.EventRepository;
import com.eventbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

// Управляет событиями. Поддерживает фильтрацию по городу, категории и дате.
@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final VenueRepository venueRepository;
    private final CategoryRepository categoryRepository;

    // Возвращает список событий с опциональной фильтрацией
    public List<EventResponse> getAll(String city, Long categoryId, LocalDate date) {
        Specification<Event> spec = Specification.where(null);

        if (city != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.join("venue").get("city"), city));
        }
        if (categoryId != null) {
            spec = spec.and((root, query, cb) ->
                    cb.equal(root.get("category").get("id"), categoryId));
        }
        if (date != null) {
            spec = spec.and((root, query, cb) ->
                    cb.between(root.get("eventDate"),
                            date.atStartOfDay(),
                            date.plusDays(1).atStartOfDay()));
        }

        return eventRepository.findAll(spec).stream()
                .map(this::toResponse)
                .toList();
    }

    public EventResponse getById(Long id) {
        Event event = eventRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Event not found: " + id));
        return toResponse(event);
    }

    // Создаёт событие. availableSeats берётся из вместимости площадки.
    public EventResponse create(EventRequest request) {
        Venue venue = venueRepository.findById(request.getVenueId())
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + request.getVenueId()));
        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Category not found: " + request.getCategoryId()));

        Event event = Event.builder()
                .title(request.getTitle())
                .description(request.getDescription())
                .eventDate(request.getEventDate())
                .price(request.getPrice())
                .availableSeats(venue.getCapacity())
                .venue(venue)
                .category(category)
                .build();

        return toResponse(eventRepository.save(event));
    }

    public void delete(Long id) {
        if (!eventRepository.existsById(id)) {
            throw new ResourceNotFoundException("Event not found: " + id);
        }
        eventRepository.deleteById(id);
    }

    // Маппинг сущности в DTO — контроллер никогда не работает напрямую с Entity
    private EventResponse toResponse(Event event) {
        return EventResponse.builder()
                .id(event.getId())
                .title(event.getTitle())
                .description(event.getDescription())
                .eventDate(event.getEventDate())
                .price(event.getPrice())
                .availableSeats(event.getAvailableSeats())
                .venueName(event.getVenue().getName())
                .venueCity(event.getVenue().getCity())
                .venueAddress(event.getVenue().getAddress())
                .categoryName(event.getCategory().getName())
                .createdAt(event.getCreatedAt())
                .build();
    }
}