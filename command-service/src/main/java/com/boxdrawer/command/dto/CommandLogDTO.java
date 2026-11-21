package com.boxdrawer.command.dto;

import java.time.LocalDateTime;

public record CommandLogDTO(
  Long id,
  Long boardId,
  String rawText,
  String action,
  String status,
  String message,
  LocalDateTime createdAt
) {}
