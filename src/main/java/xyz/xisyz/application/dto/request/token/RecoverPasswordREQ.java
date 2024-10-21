package xyz.xisyz.application.dto.request.token;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record RecoverPasswordREQ(
        @NotBlank(message = "Não pode ser vazio")
        @Pattern(
                regexp = "(?i)[a-z0-9!#$%&'*+=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
                message = "Email inválido"
        )
        String email
) {
}