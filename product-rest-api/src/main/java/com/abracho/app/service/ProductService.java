package com.abracho.app.service;

import java.time.Duration;
import java.util.Collections;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.abracho.app.exception.ProductException;
import com.abracho.app.model.ProductDto;

import reactor.core.publisher.Mono;
import reactor.util.retry.Retry;

@Service
public class ProductService implements IProductService {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    @Value("${config.uri.endpoint.similarids}")
    private String uriSimilarIds;

    @Value("${config.uri.endpoint.productid}")
    private String uriProductId;

    @Value("${config.uri.endpoint.retry.backoff}")
    private int backoffTime;

    @Value("${config.uri.endpoint.retry.maxAttempts}")
    private int maxAttempts;

    @Autowired
    private WebClient client;

    private boolean flag;

    @Override
    public Mono<String> getSimilarids(String id) {
        return client.get().uri(uriSimilarIds, Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(String.class).log()
                .cache();
    }

    @Cacheable("ProductDto")
    public Mono<ProductDto> getProductById2(String id) {

        return client.get().uri(uriProductId, Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .onStatus(HttpStatus::isError, xxx -> Mono.error(new ProductException(xxx.rawStatusCode(), "Error")))
                .bodyToMono(ProductDto.class).log()
                .retryWhen(Retry.backoff(maxAttempts, Duration.ofSeconds(backoffTime))
                        .filter(t -> t instanceof ProductException)
                        .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                            throw new ProductException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                                    "External API Services failed to process after max retries");
                        }));

    }

    @Override
    @Cacheable("ProductDto")
    public Mono<ProductDto> getProductById(String id) {

        Mono<ProductDto> cliente = client.get().uri(uriProductId, Collections.singletonMap("id", id))
                .accept(MediaType.APPLICATION_JSON)
                .retrieve()
                .bodyToMono(ProductDto.class).log()
                .cache();

        cliente.onErrorResume(WebClientResponseException.class, res -> {

            if (res.getMessage().contains("500 Internal Server")) {
                this.flag = true;
                return Mono.empty();
            } else {
                return Mono.error(new ProductException("Error External"));
            }
        });
        if (!flag) {
            return cliente
                    .retryWhen(Retry.fixedDelay(3, Duration.ofSeconds(backoffTime))
                            .filter(throwable -> throwable instanceof ProductException)
                            .onRetryExhaustedThrow((retryBackoffSpec, retrySignal) -> {
                                throw new ProductException(HttpStatus.SERVICE_UNAVAILABLE.value(),
                                        "External API Services failed to process after max retries");
                            }));

        }
        return cliente;
    }

}
