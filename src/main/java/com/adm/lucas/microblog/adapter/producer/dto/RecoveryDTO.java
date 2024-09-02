package com.adm.lucas.microblog.adapter.producer.dto;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record RecoveryDTO(
        String mailTo,
        String subject,
        String text
) {
    public static String text(String token) {
        try {
            ClassPathResource resource = new ClassPathResource("templates/mail/recovery.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template.replace("{token}", token);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public RecoveryDTO(String mailTo, String token) {
        this(
                mailTo,
                "Recuperar conta",
                text(token)
        );
    }
}