--changeset DaniilMarukha:1
CREATE TABLE IF NOT EXISTS translation
(
    id              BIGSERIAL NOT NULL,
    ip           TEXT      NOT NULL,
    source_language varchar(50),
    target_language varchar(50),
    source_text    TEXT      NOT NULL,
    translate_text TEXT      NOT NULL,

    PRIMARY KEY (id)
);