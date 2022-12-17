CREATE INDEX index_group_owner_name
    ON Groups (OwnerId, GroupName);

CREATE TABLE GroupToTelegramChannel
(
    GroupId           BIGSERIAL    NOT NULL REFERENCES Groups(GroupId) ON DELETE  CASCADE ,
    TelegramChannelId VARCHAR(255) NOT NULL,
    UNIQUE (GroupId, TelegramChannelId)
);

CREATE INDEX index_group_telegram
    ON GroupToTelegramChannel (GroupId)