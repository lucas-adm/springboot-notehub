package br.com.notehub.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ChangePasswordREQ(
        @NotBlank(message = "Não pode ser vazio")
        @Size(min = 4, max = 8, message = "Tamanho inválido")
        String password
) {
}