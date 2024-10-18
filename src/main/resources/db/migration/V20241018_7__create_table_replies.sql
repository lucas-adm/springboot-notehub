CREATE TABLE replies (
    id UUID PRIMARY KEY NOT NULL,
    user_id UUID,
    comment_id UUID NOT NULL,
    reply_id UUID,
    created_at TIMESTAMPTZ NOT NULL,
    text VARCHAR(3333) NOT NULL,
    modified_at TIMESTAMPTZ,
    modified BOOLEAN DEFAULT FALSE,
    to_user VARCHAR(255),

    CONSTRAINT fk_user_reply FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE SET NULL,
    CONSTRAINT fk_comment_reply FOREIGN KEY (comment_id) REFERENCES comments(id),
    CONSTRAINT fk_reply_id_reply FOREIGN KEY (reply_id) REFERENCES replies(id) ON DELETE SET NULL
);