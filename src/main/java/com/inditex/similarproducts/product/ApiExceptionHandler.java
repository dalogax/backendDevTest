package com.inditex.similarproducts.product;

import java.util.concurrent.TimeoutException;

import com.inditex.similarproducts.catalog.InvalidCatalogResponseException;
import io.netty.channel.ConnectTimeoutException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.codec.CodecException;
import org.springframework.core.io.buffer.DataBufferLimitException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.reactive.function.client.WebClientException;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@RestControllerAdvice
class ApiExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    @ExceptionHandler({WebClientException.class, TimeoutException.class, CodecException.class,
            InvalidCatalogResponseException.class, DataBufferLimitException.class})
    ResponseEntity<ProblemDetail> catalogFailure(Exception error) {
        HttpStatus status = HttpStatus.BAD_GATEWAY;
        String detail = "The product catalog is unavailable or returned an invalid response.";
        if (error instanceof WebClientResponseException response && response.getStatusCode().value() == 404) {
            status = HttpStatus.NOT_FOUND;
            detail = "Product not found.";
        } else if (isTimeout(error)) {
            status = HttpStatus.GATEWAY_TIMEOUT;
            detail = "The product catalog did not respond in time.";
        }
        log.warn("Similar product lookup failed: {}", error.toString());
        return ResponseEntity.status(status).body(ProblemDetail.forStatusAndDetail(status, detail));
    }

    private boolean isTimeout(Throwable error) {
        for (Throwable cause = error; cause != null; cause = cause.getCause()) {
            if (cause instanceof TimeoutException || cause instanceof ConnectTimeoutException) {
                return true;
            }
        }
        return false;
    }
}
