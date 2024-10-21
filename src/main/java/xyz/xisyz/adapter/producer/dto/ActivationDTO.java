package xyz.xisyz.adapter.producer.dto;

import xyz.xisyz.domain.user.User;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record ActivationDTO(
        String mailTo,
        String subject,
        String text
) {
    public static String text(String client, String jwt, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("template/mail/activation.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template
                    .replace("{api.client.host}", client)
                    .replace("{jwt}", jwt)
                    .replace("{username}", username);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public ActivationDTO(String client, String jwt, User user) {
        this(
                user.getEmail(),
                "Confirme o seu email",
                text(client, jwt, user.getUsername())
        );
    }
}