package org.poma.jpa.backend.service;

import org.poma.jpa.backend.entity.Notification;
import org.poma.jpa.backend.repo.NotificationRepo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class NotificationService {

    private final NotificationRepo notificationRepo;

    public NotificationService(NotificationRepo notificationRepo) {
        this.notificationRepo = notificationRepo;
    }

    public List<Notification> getAllNotifications() {
        return notificationRepo.findAllOrderByCreatedAtDesc(PageRequest.of(0, 50)).getContent();
    }

    public List<Notification> getUnreadNotifications() {
        return notificationRepo.findUnreadNotificationsOrderByCreatedAtDesc();
    }

    public long getUnreadCount() {
        return notificationRepo.countUnreadNotifications();
    }

    public Notification markAsRead(Long notificationId) {
        Notification notification = notificationRepo.findById(notificationId)
                .orElseThrow(() -> new RuntimeException("Notification not found with ID: " + notificationId));
        
        notification.setIsRead(true);
        return notificationRepo.save(notification);
    }

    public void markAllAsRead() {
        List<Notification> unreadNotifications = notificationRepo.findUnreadNotificationsOrderByCreatedAtDesc();
        for (Notification notification : unreadNotifications) {
            notification.setIsRead(true);
        }
        notificationRepo.saveAll(unreadNotifications);
    }

    public void deleteNotification(Long notificationId) {
        if (!notificationRepo.existsById(notificationId)) {
            throw new RuntimeException("Notification not found with ID: " + notificationId);
        }
        notificationRepo.deleteById(notificationId);
    }

    public void clearAllNotifications() {
        notificationRepo.deleteAll();
    }

    public Page<Notification> getNotificationsPaginated(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        return notificationRepo.findAllOrderByCreatedAtDesc(pageable);
    }
}
