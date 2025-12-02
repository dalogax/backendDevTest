package com.zara.similarproducts.infrastructure.adapter.in.rest.exception;

import com.zara.similarproducts.domain.model.*;
import com.zara.similarproducts.infrastructure.adapter.in.rest.dto.ErrorResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.server.ServerWebExchange;
import reactor.test.StepVerifier;

import java.util.concurrent.TimeoutException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @Mock
    private ServerWebExchange exchange;
    
    @Mock
    private ServerHttpRequest request;
    
    @Mock
    private org.springframework.http.server.RequestPath requestPath;
    
    private GlobalExceptionHandler exceptionHandler;
    
    @BeforeEach
    void setUp() {
        exceptionHandler = new GlobalExceptionHandler();
        when(exchange.getRequest()).thenReturn(request);
        when(request.getPath()).thenReturn(requestPath);
        when(requestPath.value()).thenReturn("/product/1/similar");
    }

    @Test
    void shouldHandleProductNotFoundException() {
        ProductId productId = ProductId.of("123");
        ProductNotFoundException exception = new ProductNotFoundException(productId);
        
        StepVerifier.create(exceptionHandler.handleProductNotFound(exception, exchange))
                .expectNextMatches(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
                    ErrorResponse body = response.getBody();
                    assertThat(body.error()).isEqualTo("PRODUCT_NOT_FOUND");
                    assertThat(body.message()).contains("Product not found: 123");
                    assertThat(body.path()).isEqualTo("/product/1/similar");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleInvalidProductIdException() {
        InvalidProductIdException exception = new InvalidProductIdException("abc");
        
        StepVerifier.create(exceptionHandler.handleInvalidProductId(exception, exchange))
                .expectNextMatches(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
                    ErrorResponse body = response.getBody();
                    assertThat(body.error()).isEqualTo("INVALID_PRODUCT_ID");
                    assertThat(body.message()).contains("Invalid product ID format: abc");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleExternalServiceException() {
        ExternalServiceException exception = new ExternalServiceException("Service unavailable");
        
        StepVerifier.create(exceptionHandler.handleExternalService(exception, exchange))
                .expectNextMatches(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
                    ErrorResponse body = response.getBody();
                    assertThat(body.error()).isEqualTo("EXTERNAL_SERVICE_ERROR");
                    assertThat(body.message()).isEqualTo("Service temporarily unavailable");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleTimeoutException() {
        TimeoutException exception = new TimeoutException("Request timeout");
        
        StepVerifier.create(exceptionHandler.handleTimeout(exception, exchange))
                .expectNextMatches(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.REQUEST_TIMEOUT);
                    ErrorResponse body = response.getBody();
                    assertThat(body.error()).isEqualTo("REQUEST_TIMEOUT");
                    assertThat(body.message()).isEqualTo("Request timeout");
                    return true;
                })
                .verifyComplete();
    }

    @Test
    void shouldHandleGenericException() {
        RuntimeException exception = new RuntimeException("Unexpected error");
        
        StepVerifier.create(exceptionHandler.handleGenericException(exception, exchange))
                .expectNextMatches(response -> {
                    assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
                    ErrorResponse body = response.getBody();
                    assertThat(body.error()).isEqualTo("INTERNAL_SERVER_ERROR");
                    assertThat(body.message()).isEqualTo("Internal server error");
                    return true;
                })
                .verifyComplete();
    }
}