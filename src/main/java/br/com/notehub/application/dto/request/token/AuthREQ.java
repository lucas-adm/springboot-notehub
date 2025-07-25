package br.com.notehub.application.dto.request.token;

import br.com.notehub.application.validation.constraints.NoForbiddenWords;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthREQ(
        @NoForbiddenWords(message = "Não pode")
        @NotBlank
        @Pattern(
                regexp = "^(?!.*[\\p{Zs}\\u00A0\\u2007\\u202F]).*$",
                message = "Não use espaços"
        )
        @Size(min = 2, max = 12, message = "Tamanho inválido")
        String username,

        @NotBlank
        @Size(min = 4, max = 255, message = "Tamanho inválido")
        String password
) {
}