package xyz.xisyz.adapter.producer.dto;

import org.springframework.core.io.ClassPathResource;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

public record EmailChangeDTO(
        String mailTo,
        String subject,
        String text
) {

    public static String text(String client, String token) {
        try {
            ClassPathResource resource = new ClassPathResource("template/mail/email-change.html");
            String template = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            return template
                    .replace("{token}", token)
                    .replace("{api.client.host}", client);
        } catch (IOException exception) {
            throw new ExceptionInInitializerError(exception);
        }
    }

    public static EmailChangeDTO of(String client, String mailTo, String token) {
        return new EmailChangeDTO(
                mailTo,
                "Redefina seu email",
                text(client, token)
        );
    }

}