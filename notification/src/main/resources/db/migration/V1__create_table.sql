CREATE TABLE IF NOT EXISTS notification
(
    notification_id VARCHAR(255) PRIMARY KEY,
    title           VARCHAR(255) NOT NULL,
    content         VARCHAR(255) NOT NULL,
    receiver_id     UUID         NOT NULL,
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    is_read         BOOLEAN                  DEFAULT FALSE,
    is_important    BOOLEAN                  DEFAULT TRUE
);
