CREATE TABLE tokens (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID UNIQUE NOT NULL,
    access_token VARCHAR(255) NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    expires_at TIMESTAMPTZ NOT NULL,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id)
);