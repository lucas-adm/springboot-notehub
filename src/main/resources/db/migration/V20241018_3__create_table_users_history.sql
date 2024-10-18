CREATE TABLE users_history (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID,
    username VARCHAR(255) NOT NULL,
    date_time TIMESTAMPTZ NOT NULL,
    field VARCHAR(255) NOT NULL,
    old_value TEXT NOT NULL,
    new_value TEXT NOT NULL,
    CONSTRAINT fk_user_history FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);