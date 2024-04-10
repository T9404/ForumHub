package com.example.public_interface.page;

import lombok.Value;
import org.springframework.data.domain.PageRequest;

import java.util.List;

@Value
public class PageResponse<T> {
    List<T> content;
    Metadata metadata;

    @Value
    public static class Metadata {
        int page;
        int size;
        long totalElements;

        public static PageResponse.Metadata createMetadata(PageRequest pageRequest, long totalElements) {
            return new PageResponse.Metadata(
                    pageRequest.getPageNumber(),
                    pageRequest.getPageSize(),
                    totalElements
            );
        }
    }
}
