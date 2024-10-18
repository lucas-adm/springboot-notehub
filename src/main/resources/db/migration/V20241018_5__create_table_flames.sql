CREATE TABLE flames (
    id UUID PRIMARY KEY,
    user_id UUID NOT NULL,
    note_id UUID NOT NULL,
    CONSTRAINT uq_user_note UNIQUE (user_id, note_id),
    CONSTRAINT fk_user FOREIGN KEY (user_id) REFERENCES users(id),
    CONSTRAINT fk_note FOREIGN KEY (note_id) REFERENCES notes(id)
);