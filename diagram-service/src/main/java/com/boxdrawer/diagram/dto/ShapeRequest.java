package com.boxdrawer.diagram.dto;

import jakarta.validation.constraints.NotBlank;

public record ShapeRequest(@NotBlank String type, String color, String label) {
}
