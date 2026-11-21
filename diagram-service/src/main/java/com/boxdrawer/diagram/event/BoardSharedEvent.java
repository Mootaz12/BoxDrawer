package com.boxdrawer.diagram.event;

public record BoardSharedEvent(String recipientEmail, String shareToken, String boardName, String sharedBy) {
}
