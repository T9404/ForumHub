package com.example.core.user.outbox.exception;

public class KafkaWaitingException extends RuntimeException {
    public KafkaWaitingException(String message, Throwable throwable) {
        super(message);
    }
}
