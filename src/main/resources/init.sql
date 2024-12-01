CREATE TABLE IF NOT EXISTS users (
    id BIGSERIAL PRIMARY KEY,
    login VARCHAR(255),
    password VARCHAR(255),
    nickname VARCHAR(255)
    );

CREATE TABLE IF NOT EXISTS bonuses (
    id BIGSERIAL PRIMARY KEY,
    amount INT,
    login VARCHAR(255)
    );