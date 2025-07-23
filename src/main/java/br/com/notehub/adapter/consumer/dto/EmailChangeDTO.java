package br.com.notehub.adapter.consumer.dto;

public record EmailChangeDTO(
        String mailTo,
        String subject,
        String text
) {
}