package xyz.xisyz.adapter.consumer.dto;

public record EmailChangeDTO(
        String mailTo,
        String subject,
        String text
) {
}