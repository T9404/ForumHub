CREATE TABLE IF NOT EXISTS category
(
    category_id          UUID PRIMARY KEY,
    previous_category_id UUID,
    name                 VARCHAR(255) NOT NULL,
    created_at           TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modification_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    creator_id           UUID         NOT NULL
);

CREATE TABLE IF NOT EXISTS topic
(
    topic_id        UUID PRIMARY KEY,
    name            VARCHAR(255) NOT NULL,
    category_id     UUID         NOT NULL REFERENCES category (category_id),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modification_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    creator_id      UUID         NOT NULL,
    status          VARCHAR(10) CHECK (status IN ('ACTIVE', 'ARCHIVED')) DEFAULT 'ACTIVE'
);

CREATE TABLE IF NOT EXISTS message
(
    message_id      UUID PRIMARY KEY,
    content         VARCHAR(255) NOT NULL,
    topic_id        UUID         NOT NULL REFERENCES topic (topic_id),
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    modification_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    creator_id      UUID         NOT NULL
);

CREATE TABLE IF NOT EXISTS attachment
(
    attachment_id UUID PRIMARY KEY,
    file_id       UUID NOT NULL,
    message_id    UUID NOT NULL REFERENCES message (message_id)
);
