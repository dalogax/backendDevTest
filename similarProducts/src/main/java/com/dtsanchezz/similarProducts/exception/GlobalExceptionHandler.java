package com.dtsanchezz.similarProducts.exception;

import com.dtsanchezz.similarProducts.exception.custom.ConsumerException;
import com.dtsanchezz.similarProducts.exception.custom.NotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.servlet.http.HttpServletRequest;

@ControllerAdvice
@Component
public class GlobalExceptionHandler {

    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<Object> handleNotFoundException(
            final HttpServletRequest req,
            final NotFoundException notFoundException
    ) {
        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(notFoundException.getMessage());
    }

    @ExceptionHandler(ConsumerException.class)
    public ResponseEntity<Object> handleConsumerException(
            final HttpServletRequest req,
            final ConsumerException consumerException
    ) {
        return ResponseEntity
                .status(HttpStatus.SERVICE_UNAVAILABLE)
                .body(consumerException.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleConsumerException(
            final HttpServletRequest req,
            final Exception exception
    ) {
        return ResponseEntity
                .status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(exception.getMessage());
    }
}
