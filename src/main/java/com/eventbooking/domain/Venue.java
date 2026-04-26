package com.eventbooking.domain;

import jakarta.persistence.*;
import lombok.*;


// Площадка проведения события (концертный зал, стадион и т.д.).
// capacity — максимальное количество мест, используется при создании события.
@Entity
@Table(name= "venues")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Venue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String address;

    @Column(nullable = false)
    private String city;

    @Column(nullable = false)
    private int capacity;
}
