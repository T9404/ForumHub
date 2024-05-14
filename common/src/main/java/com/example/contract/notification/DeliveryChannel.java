package com.example.contract.notification;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum DeliveryChannel {
    EMAIL("EMAIL"),
    ALL("ALL");

    private final String name;

    @JsonValue
    public String getValue() {
        return this.name;
    }
}
