package com.adm.lucas.microblog.adapter.consumer.dto;

public record RecoveryDTO(
        String mailTo,
        String subject,
        String text
) {
}