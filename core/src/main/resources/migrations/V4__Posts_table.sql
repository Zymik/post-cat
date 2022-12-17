CREATE TABLE GroupPosts
(
    PostId   BIGSERIAL PRIMARY KEY,
    GroupId  BIGSERIAL NOT NULL REFERENCES Groups (groupid) ON DELETE CASCADE,
    PostText TEXT      NOT NULL
);

CREATE INDEX index_group_post
    ON GroupPosts (GroupId)