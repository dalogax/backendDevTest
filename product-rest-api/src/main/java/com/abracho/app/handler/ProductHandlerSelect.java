package com.abracho.app.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.abracho.app.Utils.UtilsError;
import com.abracho.app.exception.ProductException;
import com.abracho.app.model.ProductDto;
import com.abracho.app.service.ProductService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
public class ProductHandlerSelect {
    private static final Logger log = LoggerFactory.getLogger(ProductHandlerSelect.class);

    @Autowired
    private ProductService productService;

    public Mono<ServerResponse> getProductById(ServerRequest request) {
        return productService.getProductById(request.pathVariable("productId"))
                .flatMap(p -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(p))
                .onErrorResume(WebClientRequestException.class, this::catchRequestError)
                .onErrorResume(WebClientResponseException.class, this::catchResponseError)
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    public Mono<ServerResponse> getSimilarids(ServerRequest request) {
        return productService.getSimilarids(request.pathVariable("productId"))
                .flatMap(responseMono -> ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(responseMono))
                .onErrorResume(WebClientRequestException.class, this::catchRequestError)
                .onErrorResume(WebClientResponseException.class, this::catchResponseError)
                .switchIfEmpty(ServerResponse.notFound().build());

    }

    private Mono<ServerResponse> catchResponseError(WebClientResponseException errorResponse) {
        if (errorResponse.getStatusCode().is4xxClientError()) {
            return ServerResponse.status(errorResponse.getStatusCode())
                    .bodyValue(UtilsError.bodyMsg4XX(errorResponse.getMessage(), errorResponse.getStatusCode()));
        } else if (errorResponse.getStatusCode().is5xxServerError()) {
            return ServerResponse.status(errorResponse.getStatusCode())
                    .bodyValue(UtilsError.bodyMsg5XX(errorResponse.getMessage(), errorResponse.getStatusCode()));
        }
        return Mono.error(new ProductException(errorResponse.getMessage()));

    }

    private Mono<ServerResponse> catchRequestError(WebClientRequestException errorResponse) {
        return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .bodyValue(UtilsError.bodyMsg5XX(errorResponse.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR));
    }
}
