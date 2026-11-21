CREATE TABLE share_tokens (
    token      CHAR(36)     NOT NULL PRIMARY KEY,
    board_id   BIGINT       NOT NULL,
    email      VARCHAR(255) NOT NULL,
    created_at DATETIME     NOT NULL DEFAULT CURRENT_TIMESTAMP,
    used_at    DATETIME     NULL,
    CONSTRAINT fk_share_board FOREIGN KEY (board_id) REFERENCES boards (id) ON DELETE CASCADE
);
