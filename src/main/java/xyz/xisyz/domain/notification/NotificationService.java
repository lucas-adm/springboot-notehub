package xyz.xisyz.domain.notification;

import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import xyz.xisyz.application.dto.notification.MessageNotification;
import xyz.xisyz.application.dto.response.notification.DetailNotificationRES;
import xyz.xisyz.application.dto.response.page.PageRES;
import xyz.xisyz.domain.user.User;

import java.util.UUID;

@Service
public interface NotificationService {

    void notify(User from, User to, User related, MessageNotification message);

    void readNotification(UUID id);

    PageRES<DetailNotificationRES> getNotifications(Pageable pageable, UUID idFromToken);

}