package br.com.notehub.application.dto.request.user;

import br.com.notehub.application.validation.constraints.NoForbiddenWords;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeDisplayNameREQ(
        @NoForbiddenWords(message = "Não pode")
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        @Size(min = 2, max = 24, message = "Tamanho inválido")
        String displayName
) {
}