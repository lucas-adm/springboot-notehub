package com.adm.lucas.microblog.application.dto.request.note;

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
        @NotBlank(message = "Não pode ser vazio")
        String markdown,

        boolean closed,

        boolean hidden,

        List<
                @Pattern(
                        regexp = "^(?!.*[\\u00A0\\u2007\\u202F]).*$",
                        message = "👀"
                )
                @NotBlank(message = "A tag não pode ser vazia.")
                @Size(min = 2, max = 20, message = "Tamanho inválido")
                        String> tags
) {
}