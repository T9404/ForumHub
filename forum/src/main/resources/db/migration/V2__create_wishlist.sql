CREATE TABLE IF NOT EXISTS wishlist
(
    user_id    UUID NOT NULL,
    topic_id   UUID NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (topic_id) REFERENCES topic (topic_id),
    PRIMARY KEY (user_id, topic_id)
);
