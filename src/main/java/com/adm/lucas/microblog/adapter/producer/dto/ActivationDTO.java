package com.adm.lucas.microblog.adapter.producer.dto;

import com.adm.lucas.microblog.domain.model.User;
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
    public static String text(UUID id, String username) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/mail/activation.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{username}", username).replace("{id}", id.toString());
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public ActivationDTO(User user) {
        this(
                user.getId(),
                user.getEmail(),
                "Microblog",
                text(user.getId(), user.getUsername())
        );
    }
}