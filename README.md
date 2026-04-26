# Event Booking System

A REST API backend for booking tickets to events — concerts, festivals, and sports games.

## Tech Stack

- **Java 21** + **Spring Boot 3.3**
- **Spring Security** — JWT authentication (jjwt 0.12)
- **Spring Data JPA** + **Hibernate**
- **PostgreSQL** — primary database
- **Flyway** — database migration versioning
- **Docker Compose** — runs PostgreSQL in a container
- **JUnit 5** + **Mockito** + **MockMvc** — testing

## Features

- User registration and login with JWT token issuance
- Role-based access control: `USER` and `ADMIN`
- Browse events with filtering by city and category
- Book seats with race condition protection (pessimistic locking)
- Cancel bookings with automatic seat return
- Full CRUD for events, venues, and categories (ADMIN only)

## Project Structure

```
src/main/java/com/eventbooking/
├── config/          # Security configuration
├── controller/      # REST controllers
├── domain/          # JPA entities (User, Event, Booking, ...)
├── dto/             # Request / Response objects
├── repository/      # Spring Data repositories
├── security/        # JWT filter, provider, UserDetailsService
└── service/         # Business logic

src/main/resources/
├── application.yml
└── db/migration/    # Flyway migrations V1–V4
```

## Getting Started

### Prerequisites

- Java 21+
- Maven 3.9+
- Docker Desktop

### 1. Start the database

```bash
docker-compose up -d
```

PostgreSQL will be available on port **5433**.

### 2. Run the application

```bash
mvn spring-boot:run
```

API is available at: `http://localhost:8080`

## API Reference

### Auth — public endpoints

| Method | URL | Description |
|--------|-----|-------------|
| POST | `/api/auth/register` | Register a new user |
| POST | `/api/auth/login` | Login and receive a JWT token |

### Events — GET is public, write operations require ADMIN

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/events` | List events (filters: `city`, `categoryId`) |
| GET | `/api/events/{id}` | Get event by ID |
| POST | `/api/events` | Create event |
| PUT | `/api/events/{id}` | Update event |
| DELETE | `/api/events/{id}` | Delete event |

### Bookings — require USER role

| Method | URL | Description |
|--------|-----|-------------|
| GET | `/api/bookings` | Get my bookings |
| POST | `/api/bookings` | Create a booking |
| DELETE | `/api/bookings/{id}` | Cancel a booking |

### Venues & Categories — GET is public, write operations require ADMIN

| Method | URL |
|--------|-----|
| GET / POST | `/api/venues` |
| GET / POST | `/api/categories` |

## Authentication

All protected endpoints require the following header:

```
Authorization: Bearer <jwt-token>
```

The token is returned on login or registration.

## Request Examples

```http
### Register
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@mail.com",
  "password": "123456",
  "fullName": "John Doe"
}

### Get events with filters
GET http://localhost:8080/api/events?city=Kyiv&categoryId=1

### Book seats
POST http://localhost:8080/api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "eventId": 1,
  "seatsCount": 2
}
```

## Running Tests

```bash
mvn test
```

| Test Class | Type | Count |
|---|---|---|
| `BookingServiceTest` | Unit tests (Mockito) | 6 |
| `AuthControllerTest` | HTTP tests (MockMvc) | 5 |
| `BookingControllerTest` | HTTP tests (MockMvc) | 5 |

## Database Schema

The schema is created automatically via Flyway migrations:

| Migration | Description |
|---|---|
| `V1` | `users` table |
| `V2` | `categories` and `venues` tables |
| `V3` | `events` table |
| `V4` | `bookings` table |
