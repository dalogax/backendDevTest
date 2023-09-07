package com.product.exception.handler;

import com.product.exception.InternalErrorException;
import com.product.exception.NotFoundException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class ApplicationExceptionHandlerTest {

    @InjectMocks
    private ApplicationExceptionHandler exceptionHandler;

    @Test
    void when_notFoundProduct_returnStatusNotFound() {

        NotFoundException exception = new NotFoundException("Product not found");


        ResponseEntity<String> responseEntity = exceptionHandler.handleNotFoundException(exception);

        assertThat(HttpStatus.NOT_FOUND).isEqualTo(responseEntity.getStatusCode());
        assertThat("Product not found").isEqualTo(responseEntity.getBody());
    }

    @Test
    void when_notFoundProduct_returnStatusInternalServerError() {

        InternalErrorException exception = new InternalErrorException("Internal server error");


        ResponseEntity<String> responseEntity = exceptionHandler.handleInternalErrorException(exception);

        assertThat(HttpStatus.INTERNAL_SERVER_ERROR).isEqualTo(responseEntity.getStatusCode());
        assertThat("Internal server error").isEqualTo(responseEntity.getBody());
    }
}
