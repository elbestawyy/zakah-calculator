CREATE SEQUENCE IF NOT EXISTS role_seq_id START 1;
INSERT INTO roles (id, type)
VALUES
    (nextval('role_seq_id'), 'ROLE_INDIVIDUAL'),
    (nextval('role_seq_id'), 'ROLE_COMPANY');