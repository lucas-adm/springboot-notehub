package xyz.xisyz.domain.notification;

import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.domain.user.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface NotificationService {

    void notify(User toUser, User user, MessageNotification message);

    void readNotification(UUID id);

    Page<Notification> getNotifications(Pageable pageable, UUID idFromToken);

}