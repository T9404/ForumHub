package com.example.core.converter;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

public final class OffsetDateTimeConverter {

    private OffsetDateTimeConverter() {
        throw new UnsupportedOperationException();
    }

    public static OffsetDateTime convert(Long epoch) {
        return  OffsetDateTime.ofInstant(Instant.ofEpochMilli(epoch), ZoneOffset.UTC);
    }
}
