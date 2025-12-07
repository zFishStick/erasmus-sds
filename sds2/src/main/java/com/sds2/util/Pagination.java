package com.sds2.util;

import java.util.ArrayList;
import java.util.List;

public class Pagination<T> {

    public static final int DEFAULT_PAGE_SIZE = 5;

    private final List<T> items;
    private final int page;
    private final int totalPages;
    private final int pageSize;

    public Pagination(List<T> source, int page, int pageSize) {
        this.pageSize = pageSize <= 0 ? DEFAULT_PAGE_SIZE : pageSize;

        int total = source == null ? 0 : source.size();
        this.totalPages = (int) Math.ceil((double) total / this.pageSize);
        this.page = totalPages == 0 ? 0 : clamp(page, 0, totalPages - 1);

        int from = this.page * this.pageSize;
        int to = Math.min(from + this.pageSize, total);

        if (source == null || total == 0) {
            this.items = List.of();
        } else {
            this.items = new ArrayList<>(source.subList(from, to));
        }
    }

    private int clamp(int val, int min, int max) {
        return Math.clamp(val, min, max);
    }

    public List<T> items() { return items; }
    public int page() { return page; }
    public int totalPages() { return totalPages; }
    public int pageSize() { return pageSize; }
}

