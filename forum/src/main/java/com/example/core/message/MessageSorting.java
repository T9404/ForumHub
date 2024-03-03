package com.example.core.message;

import com.example.core.topic.TopicEvent;
import com.example.rest.configuration.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum MessageSorting {
    CONTENT("content"),
    CREATED_AT("createdAt");

    private final String value;

    @JsonCreator
    public static MessageSorting fromValue(String value) {
        return Arrays.stream(MessageSorting.values())
                .filter(orderSortingType -> orderSortingType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(TopicEvent.INVALID_TOPIC_SORTING, "Invalid order sorting type"));
    }
}
