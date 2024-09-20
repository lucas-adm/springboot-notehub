package com.adm.lucas.microblog.application.dto.request.reply;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateReplyREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        @Size(max = 777, message = "Tamanho excedido")
        String text
) {
}