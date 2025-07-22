ALTER TABLE notifications ALTER COLUMN related_user_id DROP NOT NULL;

ALTER TABLE notifications DROP CONSTRAINT IF EXISTS fk_notifications_related_user;

ALTER TABLE notifications ADD CONSTRAINT fk_notifications_related_user FOREIGN KEY (related_user_id) REFERENCES users(id) ON DELETE SET NULL;