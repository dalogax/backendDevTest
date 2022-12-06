package com.similar.application.exception;

import feign.FeignException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;


@ControllerAdvice
public class SimulatedClientExceptionHandler extends ResponseEntityExceptionHandler {

    // Method for get details of error to the client:
/*
    @ExceptionHandler({ FeignException.NotFound.class })
    public ResponseEntity<Object> handleSimulatedClientNotFoundException(FeignException.NotFound ex, HandlerMethod handlerMethod, HttpServletRequest request) {
        final Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", LocalDateTime.now());
        body.put("status", HttpStatus.NOT_FOUND.value());
        body.put("method", request.getMethod());
        body.put("message", "Similar products not found :(");
        body.put("info", ex.getMessage());

        return new ResponseEntity<>(body, HttpStatus.NOT_FOUND);
    }
*/

    // Method for backendtest
    @ExceptionHandler({ FeignException.NotFound.class })
    public ResponseEntity<Object> handleSimulatedClientNotFoundException() {
        return ResponseEntity.notFound().build();
    }
}
