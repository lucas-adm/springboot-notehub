CREATE TABLE users (
    id UUID PRIMARY KEY NOT NULL,
    email VARCHAR(255) UNIQUE NOT NULL,
    username VARCHAR(255) UNIQUE NOT NULL,
    display_name VARCHAR(255) NOT NULL,
    avatar TEXT NOT NULL,
    banner TEXT,
    message VARCHAR(255),
    password VARCHAR(255) NOT NULL,
    host VARCHAR(255) NOT NULL,
    profile_private BOOLEAN DEFAULT FALSE,
    sponsor BOOLEAN DEFAULT FALSE,
    score BIGINT DEFAULT 0,
    created_at TIMESTAMPTZ NOT NULL,
    active BOOLEAN NOT NULL,
    followers_count INT DEFAULT 0,
    following_count INT DEFAULT 0
);

CREATE TABLE user_followers (
    user_id UUID REFERENCES users(id) NOT NULL,
    follower_id UUID REFERENCES users(id) NOT NULL,
    PRIMARY KEY (user_id, follower_id)
);

CREATE TABLE user_following (
    user_id UUID REFERENCES users(id) NOT NULL,
    following_id UUID REFERENCES users(id) NOT NULL,
    PRIMARY KEY (user_id, following_id)
);

CREATE INDEX idx_users_email ON users(email);
CREATE INDEX idx_users_username ON users(username);
CREATE INDEX idx_user_followers_user_id ON user_followers(user_id);
CREATE INDEX idx_user_followers_follower_id ON user_followers(follower_id);
CREATE INDEX idx_user_following_user_id ON user_following(user_id);
CREATE INDEX idx_user_following_following_id ON user_following(following_id);