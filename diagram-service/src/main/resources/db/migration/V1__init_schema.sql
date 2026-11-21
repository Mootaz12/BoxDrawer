CREATE TABLE boards (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    name       VARCHAR(100) NOT NULL,
    owner      VARCHAR(100),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE shapes (
    id       BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id BIGINT      NOT NULL,
    seq      INT         NOT NULL,
    type     VARCHAR(20) NOT NULL,
    color    VARCHAR(20),
    label    VARCHAR(100),
    pos_x    INT,
    pos_y    INT,
    CONSTRAINT fk_shape_board FOREIGN KEY (board_id) REFERENCES boards (id),
    CONSTRAINT uq_shape_seq UNIQUE (board_id, seq)
);

CREATE TABLE links (
    id            BIGINT AUTO_INCREMENT PRIMARY KEY,
    board_id      BIGINT NOT NULL,
    from_shape_id BIGINT NOT NULL,
    to_shape_id   BIGINT NOT NULL,
    label         VARCHAR(100),
    CONSTRAINT fk_link_board FOREIGN KEY (board_id) REFERENCES boards (id),
    CONSTRAINT fk_link_from FOREIGN KEY (from_shape_id) REFERENCES shapes (id),
    CONSTRAINT fk_link_to FOREIGN KEY (to_shape_id) REFERENCES shapes (id)
);

CREATE TABLE tags (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(50) NOT NULL UNIQUE
);

CREATE TABLE board_tags (
    board_id BIGINT NOT NULL,
    tag_id   BIGINT NOT NULL,
    PRIMARY KEY (board_id, tag_id),
    CONSTRAINT fk_bt_board FOREIGN KEY (board_id) REFERENCES boards (id),
    CONSTRAINT fk_bt_tag FOREIGN KEY (tag_id) REFERENCES tags (id)
);
