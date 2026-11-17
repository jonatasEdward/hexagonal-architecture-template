package com.example.orders.adapter.in.web;

import com.example.orders.application.pagination.PageResult;

import java.util.List;
import java.util.function.Function;

public record PageResponse<T>(List<T> content, int page, int size, long totalElements, int totalPages) {

    static <S, T> PageResponse<T> from(PageResult<S> result, Function<? super S, ? extends T> mapper) {
        PageResult<T> mapped = result.map(mapper);
        return new PageResponse<>(
                mapped.content(),
                mapped.page(),
                mapped.size(),
                mapped.totalElements(),
                mapped.totalPages());
    }
}
