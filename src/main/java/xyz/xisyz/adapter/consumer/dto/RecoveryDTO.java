package xyz.xisyz.adapter.consumer.dto;

public record RecoveryDTO(
        String mailTo,
        String subject,
        String text
) {
}