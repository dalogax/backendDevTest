package dev.molaya.tests.adapters.in.rest;

import dev.molaya.tests.adapters.in.rest.dto.ProductDetail;
import dev.molaya.tests.application.in.GetSimilarProductsUseCase;
import jakarta.validation.constraints.NotEmpty;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@RestController
@RequiredArgsConstructor
public class SimilarProductsController implements ProductGenApi {
    private final GetSimilarProductsUseCase useCase;
    private final RestSimilarProductsMapper mapper;

    @Override
    @GetMapping("/products/{productId}/similar")
    public Mono<ResponseEntity<Flux<ProductDetail>>> getProductSimilar(
            @NotEmpty String productId, ServerWebExchange exchange) {
        final var input = mapper.toGetSimilarProductsInput(productId);
        return useCase.getSimilarProducts(input)
                .map(mapper::toRestProductDetail)
                .as(mapper::wrapAsOkResponse);
    }
}
