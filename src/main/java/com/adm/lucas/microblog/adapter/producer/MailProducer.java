package com.adm.lucas.microblog.adapter.producer;

import com.adm.lucas.microblog.adapter.producer.dto.ActivationDTO;
import com.adm.lucas.microblog.adapter.producer.dto.RecoveryDTO;
import com.adm.lucas.microblog.domain.model.User;
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

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountActivationMessage(User user) {
        var message = new ActivationDTO(user);
        rabbitTemplate.convertAndSend("", activationRoutingKey, message);
    }

    public void publishAccountRecoveryMessage(String mailTo, String token) {
        var message = new RecoveryDTO(mailTo, token);
        rabbitTemplate.convertAndSend("", recoveryRoutingKey, message);
    }

}