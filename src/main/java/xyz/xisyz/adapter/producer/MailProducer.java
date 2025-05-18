package xyz.xisyz.adapter.producer;

import xyz.xisyz.adapter.producer.dto.ActivationDTO;
import xyz.xisyz.adapter.producer.dto.EmailChangeDTO;
import xyz.xisyz.adapter.producer.dto.PasswordChangeDTO;
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

    @Value("${broker.queue.password.name}")
    private String passwordRoutingKey;

    @Value("${broker.queue.email.name}")
    private String emailRoutingKey;

    @Value("${api.client.host}")
    private String client;

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountActivationMessage(String jwt, User user) {
        var message = new ActivationDTO(client, jwt, user);
        rabbitTemplate.convertAndSend("", activationRoutingKey, message);
    }

    public void publishAccountPasswordChangeMessage(String mailTo, String token) {
        var message = PasswordChangeDTO.of(client, mailTo, token);
        rabbitTemplate.convertAndSend("", passwordRoutingKey, message);
    }

    public void publishAccountEmailChangeMessage(String mailTo, String token) {
        var message = EmailChangeDTO.of(client, mailTo, token);
        rabbitTemplate.convertAndSend("", emailRoutingKey, message);
    }

}