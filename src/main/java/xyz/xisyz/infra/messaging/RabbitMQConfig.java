package xyz.xisyz.infra.messaging;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    @Value("${broker.queue.activation.name}")
    private String activation;

    @Value("${broker.queue.recovery.name}")
    private String recovery;

    @Bean
    public Queue activationQueue() {
        return new Queue(activation, true);
    }

    @Bean
    public Queue recoveryQueue() {
        return new Queue(recovery, true);
    }

    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        ObjectMapper objectMapper = new ObjectMapper();
        return new Jackson2JsonMessageConverter(objectMapper);
    }

}