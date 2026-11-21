package com.boxdrawer.command.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record CommandRequest(@NotNull Long boardId, @NotBlank String text) {}
