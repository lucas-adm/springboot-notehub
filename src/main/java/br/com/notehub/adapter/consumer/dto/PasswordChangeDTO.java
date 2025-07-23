package br.com.notehub.adapter.consumer.dto;

public record PasswordChangeDTO(
        String mailTo,
        String subject,
        String text
) {
}