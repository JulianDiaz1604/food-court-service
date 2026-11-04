package com.pragma.foodcourt.domain.model;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * Transport-agnostic page of domain items. Keeps pagination metadata in the domain so the use cases
 * can return paginated results without depending on any framework (Spring Data, web, etc.).
 */
@Getter
@AllArgsConstructor
public class PagedResult<T> {
    private final List<T> content;
    private final int page;
    private final int size;
    private final long totalElements;
    private final int totalPages;
}
