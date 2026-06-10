package com.dube.workflow.notification;

import com.dube.workflow.exception.ResourceNotFoundException;
import com.dube.workflow.notification.dto.NotificationResponseDTO;
import com.dube.workflow.user.User;
import com.dube.workflow.user.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class NotificationService {

    private final NotificationRepository notificationRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public NotificationService(NotificationRepository notificationRepository, UserRepository userRepository, EmailService emailService) {
        this.notificationRepository = notificationRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    @Transactional
    public void createAndSendNotification(UUID userId, String title, String message, boolean sendEmail) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // 1. Create In-App Notification
        Notification notification = new Notification();
        notification.setUser(user);
        notification.setTitle(title);
        notification.setMessage(message);
        notificationRepository.save(notification);

        // 2. Send Email if requested
        if (sendEmail && user.getEmail() != null) {
            emailService.sendEmail(user.getEmail(), title, message);
        }
    }

    @Transactional(readOnly = true)
    public List<NotificationResponseDTO> getUserNotifications(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId()).stream()
                .map(n -> new NotificationResponseDTO(n.getId(), n.getTitle(), n.getMessage(), n.isRead(), n.getCreatedAt()))
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public long getUnreadCount(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return notificationRepository.countByUserIdAndIsReadFalse(user.getId());
    }

    @Transactional
    public void markAsRead(UUID notificationId, String email) {
        Notification notification = notificationRepository.findById(notificationId)
                .orElseThrow(() -> new ResourceNotFoundException("Notification not found"));
                
        if(notification.getUser().getEmail().equals(email)) {
            notification.setRead(true);
            notificationRepository.save(notification);
        }
    }

    @Transactional
    public void markAllAsRead(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new ResourceNotFoundException("User not found"));
        List<Notification> unread = notificationRepository.findByUserIdOrderByCreatedAtDesc(user.getId())
                .stream().filter(n -> !n.isRead()).collect(Collectors.toList());
                
        unread.forEach(n -> n.setRead(true));
        notificationRepository.saveAll(unread);
    }
}