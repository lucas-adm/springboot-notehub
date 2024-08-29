package com.adm.lucas.microblog.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeDisplayNameREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        @Size(min = 4, max = 24, message = "Tamanho inválido")
        String displayName
) {
}