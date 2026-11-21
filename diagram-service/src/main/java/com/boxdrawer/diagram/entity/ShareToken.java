package com.boxdrawer.diagram.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

/** One-time download link sent in a share email. */
@Entity
@Table(name = "share_tokens")
public class ShareToken {

    @Id
    private String token = UUID.randomUUID().toString();

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "board_id")
    private Board board;

    @Column(nullable = false)
    private String email;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "used_at")
    private LocalDateTime usedAt;

    protected ShareToken() {
    }

    public ShareToken(Board board, String email) {
        this.board = board;
        this.email = email;
    }

    public String getToken() { return token; }
    public Board getBoard() { return board; }
    public String getEmail() { return email; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUsedAt() { return usedAt; }
}
