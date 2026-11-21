package com.boxdrawer.command.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "command_logs")
public class CommandLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "board_id")
    private Long boardId;

    @Column(name = "raw_text")
    private String rawText;

    private String action;
    private String status;
    private String message;

    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    public Long getId() { return id; }
    public Long getBoardId() { return boardId; }
    public void setBoardId(Long boardId) { this.boardId = boardId; }
    public String getRawText() { return rawText; }
    public void setRawText(String rawText) { this.rawText = rawText; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}
