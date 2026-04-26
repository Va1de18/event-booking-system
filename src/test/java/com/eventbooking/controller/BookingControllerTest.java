package com.eventbooking.controller;

import com.eventbooking.config.SecurityConfig;
import com.eventbooking.dto.request.BookingRequest;
import com.eventbooking.dto.response.BookingResponse;
import com.eventbooking.security.JwtTokenProvider;
import org.springframework.security.core.userdetails.UserDetailsService;
import com.eventbooking.service.BookingService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// HTTP тесты контроллера бронирования.
// @WithMockUser имитирует авторизованного пользователя без реального JWT.
@WebMvcTest(controllers = BookingController.class)
@Import(SecurityConfig.class)
@SuppressWarnings("removal")
class BookingControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private BookingService bookingService;

    @MockBean
    private JwtTokenProvider jwtTokenProvider;

    @MockBean
    private UserDetailsService userDetailsService;

    // Получение броней без авторизации — 401
    @Test
    void getMyBookings_shouldReturn401_whenNotAuthenticated() throws Exception {
        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isUnauthorized());
    }

    // Получение броней авторизованным пользователем — 200
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void getMyBookings_shouldReturn200_whenAuthenticated() throws Exception {
        BookingResponse booking = BookingResponse.builder()
                .id(1L).eventId(1L).eventTitle("Rock Fest")
                .eventDate(LocalDateTime.now().plusDays(10))
                .seatsCount(2).status("CONFIRMED")
                .build();

        when(bookingService.getMyBookings()).thenReturn(List.of(booking));

        mockMvc.perform(get("/api/bookings"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].eventTitle").value("Rock Fest"))
                .andExpect(jsonPath("$[0].status").value("CONFIRMED"));
    }

    // Создание брони — 201
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void create_shouldReturn201_whenValidRequest() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setEventId(1L);
        request.setSeatsCount(2);

        BookingResponse response = BookingResponse.builder()
                .id(1L).eventId(1L).eventTitle("Rock Fest")
                .eventDate(LocalDateTime.now().plusDays(10))
                .seatsCount(2).status("CONFIRMED")
                .build();

        when(bookingService.create(any())).thenReturn(response);

        mockMvc.perform(post("/api/bookings").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.status").value("CONFIRMED"))
                .andExpect(jsonPath("$.seatsCount").value(2));
    }

    // Создание брони с seatsCount = 0 — 400
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void create_shouldReturn400_whenSeatsCountIsZero() throws Exception {
        BookingRequest request = new BookingRequest();
        request.setEventId(1L);
        request.setSeatsCount(0); // нарушает @Positive

        mockMvc.perform(post("/api/bookings").with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    // Отмена брони — 204
    @Test
    @WithMockUser(username = "user@mail.com", roles = "USER")
    void cancel_shouldReturn204_whenValidId() throws Exception {
        doNothing().when(bookingService).cancel(1L);

        mockMvc.perform(delete("/api/bookings/1").with(csrf()))
                .andExpect(status().isNoContent());

        verify(bookingService).cancel(1L);
    }
}
