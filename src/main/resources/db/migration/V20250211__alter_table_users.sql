ALTER TABLE users ADD COLUMN provider_id VARCHAR(255) UNIQUE;

CREATE INDEX idx_users_provider_id ON users(provider_id);