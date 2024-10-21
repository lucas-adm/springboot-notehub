package xyz.xisyz.application.dto.request.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record AuthREQ(
        @NotBlank
        @Pattern(
                regexp = "^(?!.*[\\p{Zs}\\u00A0\\u2007\\u202F]).*$",
                message = "Não use espaços"
        )
        @Size(min = 4, max = 12, message = "Tamanho inválido")
        String username,

        @NotBlank
        @Size(min = 4, max = 8, message = "Tamanho inválido")
        String password
) {
}