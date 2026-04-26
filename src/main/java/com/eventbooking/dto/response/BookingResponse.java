package com.eventbooking.dto.response;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDateTime;

// Ответ с данными брони. Содержит основную информацию о событии чтобы не делать лишний запрос.
@Data
@Builder
public class BookingResponse {
    private Long id;
    private Long eventId;
    private String eventTitle;
    private LocalDateTime eventDate;
    private int seatsCount;
    private String status;
    private LocalDateTime createdAt;
}