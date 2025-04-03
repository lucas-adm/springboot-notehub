package xyz.xisyz.application.dto.request.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Null;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import xyz.xisyz.domain.user.User;

public record ChangeUserREQ(

        @NotBlank(message = "Não pode ser vazio")
        @Pattern(
                regexp = "^[a-zA-Z0-9_.]+$",
                message = "Apenas letras, números, _ e ."
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

        @Pattern(
                regexp = "^https://(([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,})(?!:)(/\\S*)?$",
                message = "Link inválido"
        )
        String avatar,

        @Pattern(
                regexp = "^https://(([a-zA-Z0-9\\-]+\\.)+[a-zA-Z]{2,})(?!:)(/\\S*)?$",
                message = "Link inválido"
        )
        String banner,

        @Size(max = 48, message = "Tamanho inválido")
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        String message,

        boolean profilePrivate

) {
    public User toUser() {
        return new User(username.toLowerCase(), displayName, avatar, banner, message, profilePrivate);
    }
}