package dev.molaya.tests.adapters.in.rest.advice;

import dev.molaya.tests.adapters.in.rest.advice.dto.ErrorType;
import dev.molaya.tests.domain.exceptions.IntegrationException;
import java.util.Optional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class SimilarProductsControllerAdvice {

    @ExceptionHandler({Exception.class})
    public ResponseEntity<ErrorType.ApiError> handleException(Exception ex) {
        log.info("Unexpected error occurred: {} - trace: ", ex.getMessage(), ex);
        return ErrorType.UNKNOWN.getErrorResponseEntity(ex.getMessage());
    }

    @ExceptionHandler({IntegrationException.class})
    public ResponseEntity<ErrorType.ApiError> handleIntegrationException(IntegrationException exception) {
        final var message = Optional.ofNullable(exception.getCause())
                .map(Throwable::getMessage)
                .orElse("not provided");
        log.info(
                "Integration error occurred: {} - internal exception [{}] - trace: ",
                exception.getMessage(),
                message,
                exception);
        return ErrorType.EXTERNAL_SERVICE.getErrorResponseEntity(exception.getMessage());
    }
}
