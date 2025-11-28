CREATE TABLE ticket_sequences
(
    id              UUID PRIMARY KEY,
    sequence_prefix VARCHAR(10) NOT NULL UNIQUE,
    current_number  INTEGER     NOT NULL     DEFAULT 0,
    last_reset_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);
