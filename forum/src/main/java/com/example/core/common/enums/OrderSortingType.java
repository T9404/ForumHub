package com.example.core.common.enums;

import com.example.core.common.exception.BusinessException;
import com.fasterxml.jackson.annotation.JsonCreator;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.Arrays;

@Getter
@AllArgsConstructor
public enum OrderSortingType {
    DESC("desc"),
    ASC("asc");

    private final String value;

    @JsonCreator
    public static OrderSortingType fromValue(String value) {
        return Arrays.stream(OrderSortingType.values())
                .filter(orderSortingType -> orderSortingType.value.equals(value))
                .findFirst()
                .orElseThrow(() -> new BusinessException(PageEvent.INVALID_ORDER_SORTING_TYPE, "Invalid order sorting type"));
    }
}
