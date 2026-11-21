package com.boxdrawer.notification.event;

public record BoardSharedEvent(String recipientEmail, String shareToken, String boardName, String sharedBy) {
}
