CREATE SCHEMA IF NOT EXISTS spring_file_storage;

CREATE TABLE IF NOT EXISTS spring_file_storage.users
(
    id        SERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL,
    status VARCHAR(50) NOT NULL
        CONSTRAINT users_status_check CHECK (status IN ('ACTIVE', 'BLOCKED'))
);

CREATE TABLE IF NOT EXISTS spring_file_storage.files
(
    id        SERIAL PRIMARY KEY,
    filename VARCHAR(100) NOT NULL,
    location  VARCHAR(100) NOT NULL,
    status    VARCHAR(20)  NOT NULL
        CONSTRAINT files_status_check CHECK (status IN ('ACTIVE', 'ARCHIVED'))
);

CREATE TABLE IF NOT EXISTS spring_file_storage.events
(
    id        SERIAL PRIMARY KEY,
    user_id   INTEGER REFERENCES spring_file_storage.users (id) ON DELETE CASCADE,
    file_id   INTEGER REFERENCES spring_file_storage.files (id) ON DELETE CASCADE,
    status    VARCHAR(20)              NOT NULL,
    timestamp timestamp with time zone NOT NULL
        CONSTRAINT events_status_check CHECK (status IN ('CREATED', 'UPDATED', 'DELETED'))
)