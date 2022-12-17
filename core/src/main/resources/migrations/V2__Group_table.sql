CREATE TABLE Groups
(
    GroupId   BIGSERIAL PRIMARY KEY,
    OwnerId     UUID         NOT NULL REFERENCES users(UserId),
    GroupName VARCHAR(255) NOT NULL,
    UNIQUE (OwnerId, GroupName)
)