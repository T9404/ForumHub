package com.example.error.custom;

public class KafkaWaitingException extends RuntimeException {
    public KafkaWaitingException(String message, Exception exception) {
        super(message, exception);
    }
}
