CREATE TABLE venues (
    id       BIGSERIAL PRIMARY KEY,
    name     VARCHAR(255) NOT NULL,
    address  VARCHAR(500) NOT NULL,
    city     VARCHAR(100) NOT NULL,
    capacity INT          NOT NULL CHECK (capacity > 0)
);

CREATE TABLE categories (
    id   BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL UNIQUE
);

INSERT INTO categories (name) VALUES
    ('Concert'),
    ('Conference'),
    ('Sports'),
    ('Theatre'),
    ('Exhibition');
