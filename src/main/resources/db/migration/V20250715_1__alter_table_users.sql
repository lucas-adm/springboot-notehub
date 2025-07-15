DROP TABLE IF EXISTS user_followers;
DROP TABLE IF EXISTS user_following;

CREATE TABLE user_follows (
  follower_id  UUID NOT NULL REFERENCES users(id),
  following_id UUID NOT NULL REFERENCES users(id),
  PRIMARY KEY (follower_id, following_id)
);

CREATE INDEX ON user_follows (follower_id);
CREATE INDEX ON user_follows (following_id);