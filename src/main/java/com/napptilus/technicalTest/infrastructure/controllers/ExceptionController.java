package com.napptilus.technicalTest.infrastructure.controllers;

import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionController {


    @ExceptionHandler(value = Exception.class)
    public ResponseEntity<ErrorDto> genericException(Throwable exception, HttpServletRequest request) {

        ErrorDto error = new ErrorDto();
        error.setCode(HttpStatus.INTERNAL_SERVER_ERROR.toString());
        error.setErrorMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);

    }

    @ExceptionHandler(value = IllegalArgumentException.class)
    public ResponseEntity<ErrorDto> illegalArgumentException(Throwable exception, HttpServletRequest request) {

        ErrorDto error = new ErrorDto();
        error.setCode(HttpStatus.BAD_REQUEST.toString());
        error.setErrorMessage(exception.getMessage());
        error.setPath(request.getRequestURI());

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);

    }


    @AllArgsConstructor
    @NoArgsConstructor
    @Getter
    @Setter
    public class ErrorDto {
        private String code;
        private String errorMessage;
        private String path;
    }
}
