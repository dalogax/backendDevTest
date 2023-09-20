package com.zara.exception.handler;

import com.zara.exception.ProductBadRequestException;
import com.zara.exception.ProductNotFoundException;
import com.zara.exception.ProductServerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import reactor.core.publisher.Mono;

@Slf4j
@Component
public class ProductErrorHandler {
    public <T> Mono<T> handleError(ClientResponse response) {
        HttpStatus status = response.statusCode();
        log.error("ProductErrorHandler::handleError() an error is handled with status {}", status);
        Mono<T> error;
        switch (status){
            case NOT_FOUND -> error = Mono.error(new ProductNotFoundException("The resource was not found"));
            case BAD_REQUEST -> error = Mono.error(new ProductBadRequestException("Invalid arguments"));
            default -> error = Mono.error(new ProductServerException("Internal server error"));
        }


        return error;
    }
}
