package xyz.xisyz.adapter.producer;

import xyz.xisyz.adapter.producer.dto.ActivationDTO;
import xyz.xisyz.adapter.producer.dto.RecoveryDTO;
import xyz.xisyz.domain.user.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class MailProducer {

    @Value("${broker.queue.activation.name}")
    private String activationRoutingKey;

    @Value("${broker.queue.recovery.name}")
    private String recoveryRoutingKey;

    @Value("${api.client.host}")
    private String client;

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountActivationMessage(String jwt, User user) {
        var message = new ActivationDTO(client, jwt, user);
        rabbitTemplate.convertAndSend("", activationRoutingKey, message);
    }

    public void publishAccountRecoveryMessage(String mailTo, String token) {
        var message = RecoveryDTO.of(client, mailTo, token);
        rabbitTemplate.convertAndSend("", recoveryRoutingKey, message);
    }

}