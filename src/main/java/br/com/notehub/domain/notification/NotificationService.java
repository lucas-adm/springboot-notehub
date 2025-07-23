package br.com.notehub.domain.notification;

import br.com.notehub.application.dto.notification.MessageNotification;
import br.com.notehub.application.dto.response.notification.DetailNotificationRES;
import br.com.notehub.application.dto.response.page.PageRES;
import br.com.notehub.domain.user.User;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public interface NotificationService {

    void notify(User from, User to, User related, MessageNotification message);

    void readNotification(UUID id);

    PageRES<DetailNotificationRES> getNotifications(Pageable pageable, UUID idFromToken);

}