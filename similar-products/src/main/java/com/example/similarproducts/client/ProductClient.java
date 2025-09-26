package com.example.similarproducts.client;

import com.example.similarproducts.config.ProductClientProperties;
import com.example.similarproducts.exception.NotFoundException;
import com.example.similarproducts.model.ProductDetail;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import io.github.resilience4j.retry.annotation.Retry;
import java.util.List;
import java.util.concurrent.TimeoutException;
import lombok.RequiredArgsConstructor;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientRequestException;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class ProductClient {

  private static final String CB_NAME = "products";
  private static final String ERR_NOT_FOUND = "Product %s not found";

  private final WebClient webClient;
  private final ProductClientProperties props;

  @CircuitBreaker(name = CB_NAME)
  @Retry(name = CB_NAME)
  public Mono<List<String>> getSimilarIds(String productId) {
    return webClient.get()
        .uri(props.getSimilarIds(), productId)
        .retrieve()
        .onStatus(s -> s.value() == 404,
            resp -> Mono.error(new NotFoundException(ERR_NOT_FOUND.formatted(productId))))
        .bodyToMono(new ParameterizedTypeReference<List<Integer>>() {
        })
        .map(list -> list.stream().map(String::valueOf).toList());
  }

  @CircuitBreaker(name = CB_NAME)
  @Retry(name = CB_NAME)
  public Mono<ProductDetail> getProduct(String id) {
    return webClient.get()
        .uri(props.getProduct(), id)
        .exchangeToMono(resp -> {
          var status = resp.statusCode();
          int code = status.value();

          if (code == 404) {
            return Mono.empty();
          }
          if (status.is2xxSuccessful()) {
            return resp.bodyToMono(ProductDetail.class);
          }
          if (status.is5xxServerError()) {
            return Mono.empty();
          }
          return resp.createException().flatMap(Mono::error);
        })
        .onErrorResume(ex ->
            (ex instanceof WebClientRequestException || ex instanceof TimeoutException)
                ? Mono.empty()
                : Mono.error(ex)
        );
  }


  public Flux<ProductDetail> getProductsInOrder(List<String> ids) {
    return Flux.fromIterable(ids)
        .concatMap(id ->
            getProduct(id)
                .onErrorResume(NotFoundException.class, e -> Mono.empty())
                .onErrorResume(TimeoutException.class, e -> Mono.empty())
        );
  }


}
