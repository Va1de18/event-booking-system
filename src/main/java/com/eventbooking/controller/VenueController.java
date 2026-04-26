package com.eventbooking.controller;

import com.eventbooking.domain.Venue;
import com.eventbooking.service.VenueService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

// GET эндпоинты открыты всем, POST и DELETE — только ADMIN.
@RestController
@RequestMapping("/api/venues")
@RequiredArgsConstructor
public class VenueController {

    private final VenueService venueService;

    @GetMapping
    public ResponseEntity<List<Venue>> getAll(@RequestParam(required = false) String city) {
        if (city != null) {
            return ResponseEntity.ok(venueService.getByCity(city));
        }
        return ResponseEntity.ok(venueService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Venue> getById(@PathVariable Long id) {
        return ResponseEntity.ok(venueService.getById(id));
    }

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Venue> create(@RequestBody Venue venue) {
        return ResponseEntity.status(HttpStatus.CREATED).body(venueService.create(venue));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        venueService.delete(id);
        return ResponseEntity.noContent().build();
    }
}