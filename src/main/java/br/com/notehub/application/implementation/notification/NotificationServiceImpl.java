package br.com.notehub.application.implementation.notification;

import br.com.notehub.application.dto.notification.MessageNotification;
import br.com.notehub.application.dto.response.notification.DetailNotificationRES;
import br.com.notehub.application.dto.response.page.PageRES;
import br.com.notehub.domain.notification.Notification;
import br.com.notehub.domain.notification.NotificationRepository;
import br.com.notehub.domain.notification.NotificationService;
import br.com.notehub.domain.user.User;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Component
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository repository;

    @Transactional
    @Override
    public void notify(User from, User to, User related, MessageNotification message) {
        if (to == null) return;
        if (Objects.equals(from.getId(), to.getId())) return;
        repository.save(new Notification(from, to, related, message.info()));
    }

    @Transactional
    @Override
    public void readNotification(UUID id) {
        Notification notification = repository.findById(id).orElseThrow(EntityNotFoundException::new);
        notification.setRead(true);
        repository.save(notification);
    }

    @Transactional
    @Override
    public PageRES<DetailNotificationRES> getNotifications(Pageable pageable, UUID idFromToken) {
        Page<Notification> notifications = repository.findAllByToId(pageable, idFromToken);
        Page<Notification> snapshot = new PageImpl<>(
                notifications.getContent().stream().map(Notification::new).toList(),
                pageable,
                notifications.getTotalElements()
        );
        notifications.forEach(notification -> readNotification(notification.getId()));
        Page<DetailNotificationRES> page = snapshot.map(DetailNotificationRES::new);
        return new PageRES<>(page);
    }

}