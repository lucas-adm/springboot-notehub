package com.adm.lucas.microblog.application.dto.response.page;

import org.springframework.data.domain.Page;

import java.util.List;

public record PageRES<T>(
        int totalPages,
        Long totalElements,
        int size,
        int page,
        boolean first,
        boolean last,
        List<T> content
) {
    public PageRES(Page<T> page) {
        this(
                page.getTotalPages(),
                page.getTotalElements(),
                page.getSize(),
                page.getNumber(),
                page.isFirst(),
                page.isLast(),
                page.getContent()
        );
    }
}