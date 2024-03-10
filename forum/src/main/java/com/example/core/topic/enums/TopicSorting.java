package com.example.core.topic.enums;

import com.example.core.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum TopicSorting {
    NAME("name"),
    CREATED_AT("createdAt");

    private final String value;

    @JsonCreator
    public static TopicSorting fromValue(String value) {
        return Arrays.stream(TopicSorting.values())
                .filter(orderSortingType -> orderSortingType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(TopicEvent.INVALID_TOPIC_SORTING, "Invalid order sorting type"));
    }
}
