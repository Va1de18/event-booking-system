package com.eventbooking;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

// Точка входа в приложение. @SpringBootApplication включает автоконфигурацию,
// сканирование компонентов и конфигурацию всего проекта.
@SpringBootApplication
public class EventBookingApplication {

    public static void main(String[] args) {
        SpringApplication.run(EventBookingApplication.class, args);
    }
}