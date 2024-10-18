CREATE TABLE notes (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID,
    created_at TIMESTAMPTZ NOT NULL,
    title VARCHAR(255) NOT NULL,
    markdown TEXT NOT NULL,
    modified BOOLEAN DEFAULT FALSE,
    modified_at TIMESTAMPTZ,
    closed BOOLEAN DEFAULT FALSE,
    hidden BOOLEAN DEFAULT FALSE,
    comments_count INT DEFAULT 0,
    flames_count INT DEFAULT 0,
    CONSTRAINT fk_user_note FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL
);

CREATE TABLE tags (
    id UUID PRIMARY KEY,
    name VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE note_tags (
    note_id UUID NOT NULL,
    tag_id UUID NOT NULL,
    PRIMARY KEY (note_id, tag_id),
    FOREIGN KEY (note_id) REFERENCES notes(id),
    FOREIGN KEY (tag_id) REFERENCES tags(id)
);