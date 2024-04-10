DROP TABLE IF EXISTS users CASCADE;

CREATE TABLE IF NOT EXISTS users
(
    user_id      UUID PRIMARY KEY,
    username     VARCHAR(100) NOT NULL UNIQUE,
    email        VARCHAR(200) NOT NULL UNIQUE,
    password     VARCHAR(100) NOT NULL,
    full_name    VARCHAR(200) NOT NULL,
    phone_number VARCHAR(100) NOT NULL
);

CREATE TABLE IF NOT EXISTS role
(
    user_id UUID         NOT NULL,
    role    VARCHAR(255) NOT NULL CHECK (role IN ('USER', 'MODERATOR', 'ADMIN', 'UNVERIFIED', 'BLOCKED')),
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    PRIMARY KEY (user_id, role)
);

CREATE TABLE IF NOT EXISTS confirmation_token
(
    token_id     UUID PRIMARY KEY,
    token        VARCHAR(255)             NOT NULL,
    user_id      UUID                     NOT NULL,
    is_confirmed BOOLEAN DEFAULT FALSE    NOT NULL,
    created_at   TIMESTAMP WITH TIME ZONE NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users (user_id)
);

CREATE TABLE IF NOT EXISTS assignment
(
    category_id UUID,
    user_id     UUID,
    FOREIGN KEY (user_id) REFERENCES users (user_id),
    PRIMARY KEY (category_id, user_id)
);

INSERT INTO users (user_id, username, email, password, full_name, phone_number)
VALUES ('f7b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b', 'admin', 'admin@gmail.com',
        '$2a$10$Ey6NQfYN2N84WCNjeqV51ui/Y98Vy4QH9wPqlXUpexib9xRBP5Y46',
        'Admin Admin', '+79677761281');

INSERT INTO role (user_id, role)
VALUES ('f7b3b3b3-3b3b-3b3b-3b3b-3b3b3b3b3b3b', 'ADMIN');
