CREATE TABLE categories
(
    id         UUID PRIMARY KEY,
    prefix     CHAR(1)      NOT NULL,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN                  DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);

CREATE TABLE priorities
(
    id         UUID PRIMARY KEY,
    prefix     CHAR(1)      NOT NULL,
    weight     SMALLINT     NOT NULL    DEFAULT 1,
    name       VARCHAR(100) NOT NULL,
    active     BOOLEAN                  DEFAULT TRUE,
    created_at TIMESTAMP WITH TIME ZONE,
    updated_at TIMESTAMP WITH TIME ZONE
);