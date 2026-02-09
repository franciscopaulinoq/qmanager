CREATE TABLE permissions
(
    id          SERIAL PRIMARY KEY,
    name        VARCHAR(255) UNIQUE NOT NULL
);

CREATE TABLE roles_permissions
(
    role_id UUID NOT NULL REFERENCES roles (id),
    permission_id SERIAL NOT NULL REFERENCES permissions (id),
    PRIMARY KEY (role_id, permission_id)
);