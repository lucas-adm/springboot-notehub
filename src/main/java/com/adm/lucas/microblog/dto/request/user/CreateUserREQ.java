package com.adm.lucas.microblog.dto.request.user;

import com.adm.lucas.microblog.model.User;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record CreateUserREQ(

        @NotBlank(message = "Não pode ser vazio")
        @Pattern(
                regexp = "(?i)[a-z0-9!#$%&'*+=?^_`{|}~-]+(?:\\.[a-z0-9!#$%&'*+=?^_`{|}~-]+)*@(?:[a-z0-9](?:[a-z0-9-]*[a-z0-9])?\\.)+[a-z0-9](?:[a-z0-9-]*[a-z0-9])?",
                message = "Email inválido"
        )
        String email,

        @NotBlank(message = "Não pode ser vazio")
        @Pattern(
                regexp = "^(?!.*[\\p{Zs}\\u00A0\\u2007\\u202F]).*$",
                message = "Não use espaços"
        )
        @Size(min = 4, max = 12, message = "Tamanho inválido")
        String username,

        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        @Size(min = 4, max = 24, message = "Tamanho inválido")
        String displayName,

        String avatar,

        @NotBlank(message = "Não pode ser vazio")
        @Size(min = 4, max = 8, message = "Tamanho inválido")
        String password

) {
    public User toUser() {
        return new User(email.toLowerCase(), username.toLowerCase(), displayName, avatar, password);
    }
}