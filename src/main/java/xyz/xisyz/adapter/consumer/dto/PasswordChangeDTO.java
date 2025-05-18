package xyz.xisyz.adapter.consumer.dto;

public record PasswordChangeDTO(
        String mailTo,
        String subject,
        String text
) {
}