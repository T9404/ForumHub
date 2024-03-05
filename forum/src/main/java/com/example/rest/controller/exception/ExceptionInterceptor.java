package com.example.rest.configuration;

import com.example.public_interface.error.ErrorResponseDto;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.event.Level;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.OffsetDateTime;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Slf4j
@RestControllerAdvice
public class ExceptionInterceptor {

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponseDto> handleBusinessException(BusinessException exception) {
        Level level = exception.getEventInfo().getLevel();
        var status = exception.getEventInfo().getStatus();
        log.atLevel(level)
                .setCause(exception)
                .log(exception.getMessage() + " with status code: " + status, exception);
        return handleCustomException(exception, status);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponseDto> handleMethodArgumentNotValidException(MethodArgumentNotValidException exception) {
        log.error(exception.getMessage(), exception);
        return handleBindValidationException(exception);
    }

    private ResponseEntity<ErrorResponseDto> handleCustomException(Exception exception, HttpStatus status) {
        return ResponseEntity.status(status).body(body(exception.getMessage(), status.value()));
    }

    private ResponseEntity<ErrorResponseDto> handleBindValidationException(BindException exception) {
        String message = IntStream.range(0, exception.getErrorCount()).mapToObj(i -> i + 1 + "." +
                exception.getAllErrors().get(i).getDefaultMessage()).collect(Collectors.joining("; "));
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body(message, 400));
    }

    private ErrorResponseDto body(String message, Integer code) {
        return new ErrorResponseDto(OffsetDateTime.now(), message, code);
    }
}
