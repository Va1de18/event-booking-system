package com.eventbooking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Ответ с данными события. Вместо вложенных объектов — плоская структура для удобства клиента.
@Data
@Builder
public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private LocalDateTime eventDate;
    private BigDecimal price;
    private int availableSeats;
    private String venueName;
    private String venueCity;
    private String venueAddress;
    private String categoryName;
    private LocalDateTime createdAt;
}
