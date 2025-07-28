ALTER TABLE users
    ADD auth_provider VARCHAR(255) DEFAULT 'INTERNAL';

update users set auth_provider = 'INTERNAL';