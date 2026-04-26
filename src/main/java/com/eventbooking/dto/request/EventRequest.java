package com.eventbooking.dto.request;

import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Данные для создания или обновления события. Используется только ADMIN-ом.
@Data
public class EventRequest {

    @NotBlank
    private String title;

    private  String description;

    @NotNull
    @Future(message = "Event date must be in thr future")
    private LocalDateTime eventDate;

    @NotNull
    @DecimalMin(value = "0.0")
    private BigDecimal price;

    @NotNull
    @Positive
    private Long venueId;

    @NotNull
    @Positive
    private Long categoryId;
}
