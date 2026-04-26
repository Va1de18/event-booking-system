package com.eventbooking.dto.request;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
// Данные для создания брони. Пользователь указывает событие и количество мест.
@Data
public class BookingRequest {

    @NotNull
    private Long eventId;

    @Positive(message = "Seats count must be grater than 0")
    private int seatsCount;
}
