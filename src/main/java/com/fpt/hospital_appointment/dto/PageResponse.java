package com.fpt.hospital_appointment.dto;

import org.springframework.data.domain.Page;
import java.util.List;

public class PageResponse<T> {
    private List<T> content;
    private int pageNumber;
    private int pageSize;
    private long totalElements;
    private int totalPages;
    private boolean last;

    public static <T> PageResponse<T> from(Page<T> page) {
        return new PageResponse<>(page.getContent(), page.getNumber(), page.getSize(), page.getTotalElements(), page.getTotalPages(), page.isLast());
    }

    public List<T> getContent() {
        return this.content;
    }

    public int getPageNumber() {
        return this.pageNumber;
    }

    public int getPageSize() {
        return this.pageSize;
    }

    public long getTotalElements() {
        return this.totalElements;
    }

    public int getTotalPages() {
        return this.totalPages;
    }

    public boolean isLast() {
        return this.last;
    }

    public void setContent(final List<T> content) {
        this.content = content;
    }

    public void setPageNumber(final int pageNumber) {
        this.pageNumber = pageNumber;
    }

    public void setPageSize(final int pageSize) {
        this.pageSize = pageSize;
    }

    public void setTotalElements(final long totalElements) {
        this.totalElements = totalElements;
    }

    public void setTotalPages(final int totalPages) {
        this.totalPages = totalPages;
    }

    public void setLast(final boolean last) {
        this.last = last;
    }

    public PageResponse() {
    }

    public PageResponse(final List<T> content, final int pageNumber, final int pageSize, final long totalElements, final int totalPages, final boolean last) {
        this.content = content;
        this.pageNumber = pageNumber;
        this.pageSize = pageSize;
        this.totalElements = totalElements;
        this.totalPages = totalPages;
        this.last = last;
    }
}
