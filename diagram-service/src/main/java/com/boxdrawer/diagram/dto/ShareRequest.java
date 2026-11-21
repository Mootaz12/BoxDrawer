package com.boxdrawer.diagram.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record ShareRequest(@NotBlank @Email String email) {
}
