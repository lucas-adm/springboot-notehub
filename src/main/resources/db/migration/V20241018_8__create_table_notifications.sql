CREATE TABLE notifications (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID NOT NULL,
    from_user_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    read BOOLEAN DEFAULT FALSE,
    info VARCHAR(666) NOT NULL,

    FOREIGN KEY (user_id) REFERENCES users(id),
    FOREIGN KEY (from_user_id) REFERENCES users(id)
);