package org.poma.jpa.backend.repo;

import org.poma.jpa.backend.entity.Notification;
import org.poma.jpa.backend.entity.PriceAlert;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface NotificationRepo extends JpaRepository<Notification, Long> {

    List<Notification> findByIsReadOrderByCreatedAtDesc(Boolean isRead);

    List<Notification> findByIsReadFalseOrderByCreatedAtDesc();

    List<Notification> findByAlertOrderByCreatedAtDesc(PriceAlert alert);

    @Query("SELECT COUNT(n) FROM Notification n WHERE n.isRead = false")
    long countUnreadNotifications();

    @Query("SELECT n FROM Notification n ORDER BY n.createdAt DESC")
    Page<Notification> findAllOrderByCreatedAtDesc(Pageable pageable);

    @Query("SELECT n FROM Notification n WHERE n.isRead = false ORDER BY n.createdAt DESC")
    List<Notification> findUnreadNotificationsOrderByCreatedAtDesc();

    boolean existsByAlertAndIsReadFalse(PriceAlert alert);

    boolean existsByAlertAndCreatedAtAfter(PriceAlert alert, LocalDateTime after);

    @Query("SELECT n FROM Notification n WHERE n.alert.id = :alertId AND n.isRead = false")
    List<Notification> findUnreadNotificationsByAlertId(@Param("alertId") Long alertId);
}
