package com.adm.lucas.microblog.application.implementation;

import com.adm.lucas.microblog.application.dto.notification.MessageNotification;
import com.adm.lucas.microblog.domain.notification.Notification;
import com.adm.lucas.microblog.domain.notification.NotificationRepository;
import com.adm.lucas.microblog.domain.notification.NotificationService;
import com.adm.lucas.microblog.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Override
    public void notify(User toUser, User user, MessageNotification message) {
        repository.save(new Notification(toUser, user, message.info()));
    }

    @Override
    public void readNotification(UUID id) {
        Notification notification = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        notification.setRead(true);
        repository.save(notification);
    }

    @Override
    public Page<Notification> getNotifications(Pageable pageable, UUID idFromToken) {
        Page<Notification> notifications = repository.findAllByUserId(pageable, idFromToken);
        notifications.forEach(notification -> readNotification(notification.getId()));
        return notifications;
    }

}