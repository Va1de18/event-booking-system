package com.eventbooking.service;

import com.eventbooking.domain.Venue;
import com.eventbooking.exception.ResourceNotFoundException;
import com.eventbooking.repository.VenueRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

// Управляет площадками. Только ADMIN может создавать и удалять.
@Service
@RequiredArgsConstructor
public class VenueService {

    private final VenueRepository venueRepository;

    public List<Venue> getAll() {
        return venueRepository.findAll();
    }

    public List<Venue> getByCity(String city) {
        return venueRepository.findByCity(city);
    }

    public Venue getById(Long id) {
        return venueRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Venue not found: " + id));
    }

    public Venue create(Venue venue) {
        return venueRepository.save(venue);
    }

    public void delete(Long id) {
        if (!venueRepository.existsById(id)) {
            throw new ResourceNotFoundException("Venue not found: " + id);
        }
        venueRepository.deleteById(id);
    }
}