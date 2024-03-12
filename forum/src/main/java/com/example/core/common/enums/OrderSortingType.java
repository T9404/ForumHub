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
        String lowerCaseValue = value.toLowerCase();
        return Arrays.stream(OrderSortingType.values())
                .filter(orderSortingType -> orderSortingType.value.equals(lowerCaseValue))
                .findFirst()
                .orElseThrow(() -> new BusinessException(PageEvent.INVALID_ORDER_SORTING_TYPE, "Invalid order sorting type"));
    }
}
