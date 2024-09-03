package com.adm.lucas.microblog.adapter.producer.dto;

import com.adm.lucas.microblog.domain.user.User;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.UUID;

public record ActivationDTO(
        UUID id,
        String mailTo,
        String subject,
        String text
) {
    public static String text(String server, String client, UUID id, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("template/mail/activation.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template
                    .replace("{api.server.host}", server)
                    .replace("{api.client.host}", client)
                    .replace("{id}", id.toString())
                    .replace("{username}", username);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public ActivationDTO(String server, String client, User user) {
        this(
                user.getId(),
                user.getEmail(),
                "Microblog",
                text(server, client, user.getId(), user.getUsername())
        );
    }
}