ALTER TABLE conference
ADD COLUMN IF NOT EXISTS start_at TIMESTAMP NOT NULL;
