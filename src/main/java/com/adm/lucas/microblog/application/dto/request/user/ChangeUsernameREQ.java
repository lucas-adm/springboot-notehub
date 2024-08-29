package com.adm.lucas.microblog.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeUsernameREQ(
        @NotBlank(message = "Não pode ser vazio")
        @Pattern(
                regexp = "^(?!.*[\\p{Zs}\\u00A0\\u2007\\u202F]).*$",
                message = "Não use espaços"
        )
        @Size(min = 4, max = 12, message = "Tamanho inválido")
        String username
) {
}