package product.externalApi.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.ClientResponse;
import org.springframework.web.reactive.function.client.WebClient;
import product.dto.ProductDetail;
import product.externalApi.ExternalApi;
import reactor.core.publisher.Mono;

import java.time.Duration;


@Component
@Slf4j
public class ExternalApiImpl implements ExternalApi {

    @Value("${existing-apis.base-url}")
    private String BASE_URL;

    @Value("${existing-apis.similarIds}")
    private String SIMILAR_ID;

    @Autowired
    private WebClient webClient;

    private Mono<Throwable> handleErrors(ClientResponse response ){
        return response.bodyToMono(String.class).flatMap(body -> {
            log.error("Product not found");
            return Mono.error(new Exception());
        });
    }

    @Override
    public String[] getSimilarProducts(String productId) {
        final String uri = BASE_URL + productId + SIMILAR_ID;
        return webClient.get()
                    .uri(uri)
                    .retrieve()
                    .onStatus(HttpStatus::is4xxClientError, this::handleErrors)
                    .bodyToMono(String[].class)
                    .timeout(Duration.ofSeconds(5L))
                    .block();
    }

    @Override
    public ProductDetail getProduct(String productId) {

        final String uri = BASE_URL + productId;

        return webClient.get()
                .uri(uri)
                .retrieve()
                .onStatus(HttpStatus::is4xxClientError, this::handleErrors)
                .bodyToMono(ProductDetail.class)
                .timeout(Duration.ofSeconds(5L))
                .block();
    }
}
