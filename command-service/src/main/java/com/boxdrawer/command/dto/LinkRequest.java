package com.boxdrawer.command.dto;

public record LinkRequest(int fromSeq, int toSeq, String label) {
}
