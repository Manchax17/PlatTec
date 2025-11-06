CREATE TABLE IF NOT EXISTS messages (
    id SERIAL PRIMARY KEY,
    content TEXT NOT NULL,
    sender VARCHAR(255) NOT NULL,
    timestamp BIGINT NOT NULL
);