CREATE TABLE ticket_sequences
(
    id              UUID PRIMARY KEY,
    sequence_prefix VARCHAR(10) NOT NULL UNIQUE,
    current_number  INTEGER     NOT NULL     DEFAULT 0,
    last_reset_at   TIMESTAMP WITH TIME ZONE DEFAULT NOW()
);

CREATE TABLE tickets
(
    id          UUID PRIMARY KEY,
    code        VARCHAR(20)              NOT NULL,
    status      VARCHAR(20)              NOT NULL CHECK (status IN ('WAITING', 'IN_PROGRESS', 'CLOSED', 'EXPIRED')),
    issue_date  DATE                     NOT NULL DEFAULT CURRENT_DATE,
    priority_id UUID                     NOT NULL REFERENCES priorities (id),
    category_id UUID                     NOT NULL REFERENCES categories (id),
    call_count  INT                      NOT NULL DEFAULT 0,

    created_at  TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT NOW(),
    called_at   TIMESTAMP WITH TIME ZONE,
    closed_at   TIMESTAMP WITH TIME ZONE
);

CREATE UNIQUE INDEX idx_tickets_code_date ON tickets (code, issue_date);
