package com.boxdrawer.command.dto;

public record CommandResponse(
  Long logId,
  String action,
  String status,
  String message,
  Object board
) {}
