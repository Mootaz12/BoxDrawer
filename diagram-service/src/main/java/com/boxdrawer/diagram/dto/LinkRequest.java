package com.boxdrawer.diagram.dto;

import jakarta.validation.constraints.NotNull;

public record LinkRequest(@NotNull Integer fromSeq, @NotNull Integer toSeq, String label) {
}
