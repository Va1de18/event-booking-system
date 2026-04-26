CREATE TABLE bookings (
    id          BIGSERIAL PRIMARY KEY,
    user_id     BIGINT      NOT NULL REFERENCES users (id),
    event_id    BIGINT      NOT NULL REFERENCES events (id),
    seats_count INT         NOT NULL CHECK (seats_count > 0),
    status      VARCHAR(20) NOT NULL DEFAULT 'CONFIRMED',
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_bookings_user_id  ON bookings (user_id);
CREATE INDEX idx_bookings_event_id ON bookings (event_id);
