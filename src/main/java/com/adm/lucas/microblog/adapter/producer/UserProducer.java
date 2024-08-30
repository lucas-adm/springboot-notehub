package com.adm.lucas.microblog.adapter.producer;

import com.adm.lucas.microblog.adapter.producer.dto.ActivationDTO;
import com.adm.lucas.microblog.domain.model.User;
import lombok.RequiredArgsConstructor;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserProducer {

    @Value("${broker.queue.email.name}")
    private String routingKey;

    private final RabbitTemplate rabbitTemplate;

    public void publishAccountActivationMessage(User user) {
        var message = new ActivationDTO(user);
        rabbitTemplate.convertAndSend("", routingKey, message);
    }

}