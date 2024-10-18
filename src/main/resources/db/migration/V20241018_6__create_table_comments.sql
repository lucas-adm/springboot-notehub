CREATE TABLE comments (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID,
    note_id UUID NOT NULL,
    created_at TIMESTAMPTZ NOT NULL,
    modified_at TIMESTAMPTZ,
    text VARCHAR(3333) NOT NULL,
    modified BOOLEAN DEFAULT FALSE,
    replies_count INT DEFAULT 0,
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_note FOREIGN KEY (note_id) REFERENCES notes(id)
);