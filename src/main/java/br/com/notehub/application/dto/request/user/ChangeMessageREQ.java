package br.com.notehub.application.dto.request.user;

import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record ChangeMessageREQ(
        @Size(max = 48, message = "Tamanho inválido")
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        String message
) {
}