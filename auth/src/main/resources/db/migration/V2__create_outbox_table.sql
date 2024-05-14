CREATE TABLE IF NOT EXISTS role_outbox
(
    id          UUID PRIMARY KEY,
    status      VARCHAR(10)              NOT NULL check (status in ('NEW', 'PENDING', 'SENT', 'ERROR')),
    user_id     UUID                     NOT NULL,
    created_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    updated_at  TIMESTAMP WITH TIME ZONE NOT NULL,
    retry_count INT                               DEFAULT 0,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);
