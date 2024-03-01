CREATE TABLE IF NOT EXISTS file (
    file_id UUID PRIMARY KEY,
    name VARCHAR(255),
    size BIGINT  NOT NULL,
    time_created TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

COMMENT ON COLUMN file.size IS 'Size of the file in bytes';
