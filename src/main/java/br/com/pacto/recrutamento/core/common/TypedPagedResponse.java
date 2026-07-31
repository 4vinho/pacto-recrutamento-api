package br.com.pacto.recrutamento.core.common;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TypedPagedResponse<T> extends TypedResponse<List<T>> {
    private final int page;
    private final int pageSize;
    private final long totalItems;

    public TypedPagedResponse(
            int statusCode,
            String message,
            List<T> data,
            int page,
            int pageSize,
            long totalItems
    ) {
        super(statusCode, message, immutableCopy(data));
        validatePagination(page, pageSize, totalItems);
        this.page = page;
        this.pageSize = pageSize;
        this.totalItems = totalItems;
    }

    private static void validatePagination(int page, int pageSize, long totalItems) {
        if (page < 0) {
            throw new IllegalArgumentException("A página não pode ser negativa.");
        }
        if (pageSize <= 0) {
            throw new IllegalArgumentException("O tamanho da página deve ser maior que zero.");
        }
        if (totalItems < 0) {
            throw new IllegalArgumentException("O total de itens não pode ser negativo.");
        }
    }

    private static <T> List<T> immutableCopy(List<T> data) {
        if (data == null) {
            return Collections.emptyList();
        }
        return Collections.unmodifiableList(new ArrayList<>(data));
    }

    public int getPage() {
        return page;
    }

    public int getPageSize() {
        return pageSize;
    }

    public long getTotalItems() {
        return totalItems;
    }

    public int getTotalPages() {
        return (int) Math.ceil((double) totalItems / pageSize);
    }
}
