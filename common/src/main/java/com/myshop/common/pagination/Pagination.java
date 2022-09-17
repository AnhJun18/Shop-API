package com.myshop.common.pagination;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * The page object.
 *
 * @param <T> the type of elements in this object
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Pagination<T> {
    @JsonProperty("total")
    private final long total;

    @JsonProperty("page")
    private final int page;

    @JsonProperty("size")
    private final int size;

    @JsonProperty("items")
    private final List<T> items;

    /**
     * Creates {@link Pagination page} from the given list of items, current page, the page size and total item.
     *
     * @param items the given list of items.
     * @param page  the given current page.
     * @param size  the given current page size.
     * @param total the given total number item.
     */
    public Pagination(List<T> items, int page, int size, long total) {
        if (page < 1) {
            throw new IllegalArgumentException("The page number must not be less than 1.");
        }

        if (size < 1) {
            throw new IllegalArgumentException("The page size must not be less than 1.");
        }

//        int count = (page - 1) * size;
//        if (total < 0 || total < count) {
//            throw new IllegalArgumentException("The total item must not be less than 0 or too small.");
//        }

        this.total = total;
        this.items = items;
        this.page = page;
        this.size = size;
    }

    /**
     * Creates {@link Pagination page} from the given list of items, the current page,
     * the page size and the total of items.
     *
     * @param items the given list of items in the page.
     * @param page  the given current page.
     * @param size  the given page size.
     * @param total the given total of items.
     * @return the {@link Pagination page} object.
     */
    public static <T> Pagination<T> create(List<T> items, int page, int size, long total) {
        return new Pagination<>(items, page, size, total);
    }

    /**
     * Obtains the total items.
     *
     * @return the total items.
     */
    public long total() {
        return total;
    }

    /**
     * Obtains the total pages.
     *
     * @return the total pages.
     */
    public int totalPages() {
        return (int) Math.ceil((double) total / (double) size);
    }

    /**
     * Obtains the current page number.
     *
     * @return the current page number.
     */
    public int page() {
        return page;
    }

    /**
     * Obtains the page size.
     *
     * @return the page size.
     */
    public int size() {
        return size;
    }

    /**
     * Obtains the list of items of current page.
     *
     * @return the list of items of current page.
     */
    public List<T> items() {
        return items == null ? Collections.emptyList() : items;
    }

    /**
     * Checks if there is at least one page after current page.
     *
     * @return {@code true} if there is at least one page after current page, otherwise {@code false}.
     */
    public boolean hasNext() {
        return (page + 1 <= totalPages());
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Pagination<?> that = (Pagination<?>) obj;
        return total == that.total
                && page == that.page
                && size == that.size
                && Objects.equals(items, that.items);
    }

    @Override
    public int hashCode() {
        return Objects.hash(total, page, size, items);
    }

    @Override
    public String toString() {
        return String.format("Pagination %d of %d containing %d items", page, totalPages(), items().size());
    }
}
