package com.dube.workflow.notification.dto;

import java.time.LocalDateTime;
import java.util.UUID;

public class NotificationResponseDTO {
    private UUID id;
    private String title;
    private String message;
    private boolean isRead;
    private LocalDateTime createdAt;

    public NotificationResponseDTO(UUID id, String title, String message, boolean isRead, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.message = message;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    // Getters
    public UUID getId() { return id; }
    public String getTitle() { return title; }
    public String getMessage() { return message; }
    public boolean isRead() { return isRead; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}