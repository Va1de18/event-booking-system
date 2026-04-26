package com.eventbooking.domain;

import jakarta.persistence.*;
import lombok.*;

// Категория события (Concert, Sports, Theatre и т.д.).
// Предзаполняется через Flyway миграцию V2.
@Entity
@Table(name= "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String name;
}
