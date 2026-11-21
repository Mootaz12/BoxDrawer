CREATE TABLE command_logs (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id   BIGINT       NOT NULL,
    raw_text   VARCHAR(500) NOT NULL,
    action     VARCHAR(30)  NOT NULL,
    status     VARCHAR(20)  NOT NULL,
    message    VARCHAR(500),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);
