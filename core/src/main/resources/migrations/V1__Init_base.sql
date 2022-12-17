CREATE TABLE users
(
    UserId     UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    TelegramId BIGINT NOT NULL UNIQUE
);

CREATE INDEX telegram_id_on_users
    ON users (TelegramId);