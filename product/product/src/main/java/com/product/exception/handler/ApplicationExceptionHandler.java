package com.product.exception.handler;

import com.product.exception.InternalErrorException;
import com.product.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class ApplicationExceptionHandler {
    
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<String> handleNotFoundException(final NotFoundException ex){
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(String.format(ex.getMessage()));
    }

    @ExceptionHandler(InternalErrorException.class)
    public ResponseEntity<String> handleInternalErrorException(final InternalErrorException ex){
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(String.format(ex.getMessage()));
    }
}
