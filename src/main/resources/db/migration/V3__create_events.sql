CREATE TABLE events (
    id               BIGSERIAL PRIMARY KEY,
    title            VARCHAR(255)   NOT NULL,
    description      TEXT,
    event_date       TIMESTAMP      NOT NULL,
    price            NUMERIC(10, 2) NOT NULL CHECK (price >= 0),
    available_seats  INT            NOT NULL CHECK (available_seats >= 0),
    venue_id         BIGINT         NOT NULL REFERENCES venues (id),
    category_id      BIGINT         NOT NULL REFERENCES categories (id),
    created_at       TIMESTAMP      NOT NULL DEFAULT NOW()
);

CREATE INDEX idx_events_event_date ON events (event_date);
CREATE INDEX idx_events_venue_id   ON events (venue_id);
CREATE INDEX idx_events_category_id ON events (category_id);
