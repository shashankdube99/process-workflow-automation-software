package com.dube.workflow.notification;

import com.dube.workflow.notification.dto.NotificationResponseDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/notifications")
public class NotificationController {

    private final NotificationService notificationService;

    public NotificationController(NotificationService notificationService) {
        this.notificationService = notificationService;
    }

    @GetMapping
    public ResponseEntity<List<NotificationResponseDTO>> getMyNotifications(Authentication auth) {
        return ResponseEntity.ok(notificationService.getUserNotifications(auth.getName()));
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Map<String, Long>> getUnreadCount(Authentication auth) {
        return ResponseEntity.ok(Map.of("count", notificationService.getUnreadCount(auth.getName())));
    }

    @PatchMapping("/{id}/read")
    public ResponseEntity<Map<String, String>> markAsRead(@PathVariable UUID id, Authentication auth) {
        notificationService.markAsRead(id, auth.getName());
        return ResponseEntity.ok(Map.of("message", "Marked as read"));
    }

    @PatchMapping("/read-all")
    public ResponseEntity<Map<String, String>> markAllAsRead(Authentication auth) {
        notificationService.markAllAsRead(auth.getName());
        return ResponseEntity.ok(Map.of("message", "All marked as read"));
    }
}