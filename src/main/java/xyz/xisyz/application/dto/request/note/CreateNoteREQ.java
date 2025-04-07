package xyz.xisyz.application.dto.request.note;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.util.List;

public record CreateNoteREQ(
        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @NotBlank(message = "Não pode ser vazio")
        @Size(min = 4, max = 48, message = "Tamanho inválido")
        String title,

        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        @Size(max = 255, message = "Além do limite")
        String description,

        @Pattern(
                regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                message = "👀"
        )
        String markdown,

        boolean closed,

        boolean hidden,

        @Size(max = 12, message = "Capacidade máxima excedida")
        List<
                @NotBlank(message = "Não pode ser vazio")
                @Pattern(
                        regexp = "^(?!.*[\\p{Zs}\\u00A0\\u2007\\u202F]).*$",
                        message = "Não use espaços"
                )
                @Size(min = 2, max = 20, message = "Tamanho inválido")
                        String> tags
) {
}