package br.com.notehub.adapter.consumer.dto;

import java.util.UUID;

public record ActivationDTO(
        UUID id,
        String mailTo,
        String subject,
        String text
) {
}