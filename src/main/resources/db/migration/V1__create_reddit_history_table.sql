CREATE TABLE reddit_posts (
    id bigserial NOT NULL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    url    VARCHAR(255) NOT NULL,
    score  INTEGER NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);