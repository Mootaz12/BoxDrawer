package com.boxdrawer.diagram.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;

public record GridRequest(@NotBlank String type, String color,
                          @Min(1) @Max(20) int rows, @Min(1) @Max(20) int cols) {
}
