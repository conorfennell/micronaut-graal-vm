CREATE TABLE IF NOT EXISTS CLOZED_FLASHCARD (
    id uuid NOT NULL DEFAULT uuid_generate_v4() PRIMARY KEY,
    chunk_id uuid NOT NULL,
    sentence TEXT NOT NULL,
    clozed_positions TEXT NOT NULL,
    updated_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    CONSTRAINT fk_chunk FOREIGN KEY(chunk_id) REFERENCES chunk(id) ON DELETE CASCADE
);
