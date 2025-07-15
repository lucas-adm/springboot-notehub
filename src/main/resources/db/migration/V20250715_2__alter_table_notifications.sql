ALTER TABLE notifications
DROP CONSTRAINT IF EXISTS notifications_from_user_id_fkey,
DROP COLUMN IF EXISTS from_user_id;

ALTER TABLE notifications ALTER COLUMN info TYPE jsonb USING info::jsonb;