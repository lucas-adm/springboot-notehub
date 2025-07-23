ALTER TABLE notifications
    DROP CONSTRAINT IF EXISTS fk_notifications_to_user,
    DROP CONSTRAINT IF EXISTS fk_notifications_from_user,
    DROP CONSTRAINT IF EXISTS fk_notifications_related_user;

ALTER TABLE notifications RENAME COLUMN user_id TO to_user_id;
ALTER TABLE notifications ALTER COLUMN to_user_id SET NOT NULL;

ALTER TABLE notifications
    ADD COLUMN from_user_id UUID NOT NULL,
    ADD COLUMN related_user_id UUID NOT NULL;

ALTER TABLE notifications
    ADD CONSTRAINT fk_notifications_to_user FOREIGN KEY (to_user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_notifications_from_user FOREIGN KEY (from_user_id) REFERENCES users(id) ON DELETE CASCADE,
    ADD CONSTRAINT fk_notifications_related_user FOREIGN KEY (related_user_id) REFERENCES users(id) ON DELETE CASCADE;

CREATE INDEX IF NOT EXISTS idx_notifications_to_user ON notifications(to_user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_from_user ON notifications(from_user_id);
CREATE INDEX IF NOT EXISTS idx_notifications_related_user ON notifications(related_user_id);