# Event Booking System

REST API для бронирования билетов на события — концерты, фестивали, спортивные мероприятия.

## Стек технологий

- **Java 21** + **Spring Boot 3.3**
- **Spring Security** — JWT аутентификация (jjwt 0.12)
- **Spring Data JPA** + **Hibernate** — работа с БД
- **PostgreSQL** — основная база данных
- **Flyway** — версионирование миграций
- **Docker Compose** — запуск PostgreSQL в контейнере
- **JUnit 5** + **Mockito** + **MockMvc** — тесты

## Возможности

- Регистрация и логин с выдачей JWT токена
- Роли: `USER` и `ADMIN`
- Просмотр событий с фильтрацией по городу и категории
- Бронирование мест с защитой от гонки (pessimistic lock)
- Отмена бронирования с возвратом мест
- CRUD для событий, площадок и категорий (только ADMIN)

## Структура проекта

```
src/main/java/com/eventbooking/
├── config/          # SecurityConfig
├── controller/      # REST контроллеры
├── domain/          # JPA сущности (User, Event, Booking, ...)
├── dto/             # Request / Response объекты
├── repository/      # Spring Data репозитории
├── security/        # JWT фильтр, провайдер, UserDetailsService
└── service/         # Бизнес-логика

src/main/resources/
├── application.yml
└── db/migration/    # Flyway миграции V1–V4
```

## Быстрый старт

### 1. Требования

- Java 21+
- Maven 3.9+
- Docker Desktop

### 2. Запуск базы данных

```bash
docker-compose up -d
```

PostgreSQL запустится на порту **5433**.

### 3. Запуск приложения

```bash
mvn spring-boot:run
```

API доступен по адресу: `http://localhost:8080`

## API эндпоинты

### Auth (публичные)

| Метод | URL | Описание |
|-------|-----|----------|
| POST | `/api/auth/register` | Регистрация |
| POST | `/api/auth/login` | Логин → JWT токен |

### Events (GET — публичные, остальные — ADMIN)

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/events` | Список событий (фильтры: `city`, `categoryId`) |
| GET | `/api/events/{id}` | Событие по ID |
| POST | `/api/events` | Создать событие |
| PUT | `/api/events/{id}` | Обновить событие |
| DELETE | `/api/events/{id}` | Удалить событие |

### Bookings (USER)

| Метод | URL | Описание |
|-------|-----|----------|
| GET | `/api/bookings` | Мои брони |
| POST | `/api/bookings` | Забронировать |
| DELETE | `/api/bookings/{id}` | Отменить бронь |

### Venues & Categories (GET — публичные, остальные — ADMIN)

| Метод | URL |
|-------|-----|
| GET/POST | `/api/venues` |
| GET/POST | `/api/categories` |

## Аутентификация

Все защищённые эндпоинты требуют заголовок:

```
Authorization: Bearer <jwt-token>
```

Токен получается при логине или регистрации.

## Примеры запросов

```http
### Регистрация
POST http://localhost:8080/api/auth/register
Content-Type: application/json

{
  "email": "user@mail.com",
  "password": "123456",
  "fullName": "Test User"
}

### Получить события
GET http://localhost:8080/api/events?city=Kyiv&categoryId=1

### Забронировать
POST http://localhost:8080/api/bookings
Authorization: Bearer <token>
Content-Type: application/json

{
  "eventId": 1,
  "seatsCount": 2
}
```

## Тесты

```bash
mvn test
```

- `BookingServiceTest` — 6 unit тестов сервиса (Mockito)
- `AuthControllerTest` — 5 HTTP тестов контроллера (MockMvc)
- `BookingControllerTest` — 5 HTTP тестов контроллера (MockMvc)

## База данных

Схема создаётся автоматически через Flyway миграции:

- `V1` — таблицы `users`, `roles`
- `V2` — таблицы `categories`, `venues`
- `V3` — таблица `events`
- `V4` — таблица `bookings`
