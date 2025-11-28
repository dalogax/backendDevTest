package dev.molaya.tests.adapters.in.rest;

import dev.molaya.tests.adapters.in.rest.gen.openapi.ProductGenApi;
import dev.molaya.tests.adapters.in.rest.gen.openapi.dto.ProductDetail;
import dev.molaya.tests.application.in.GetSimilarProductsUseCase;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
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
    @GetMapping(ProductGenApi.PATH_GET_PRODUCT_SIMILAR)
    public Mono<ResponseEntity<Flux<ProductDetail>>> getProductSimilar(
            @NotNull @Parameter(name = "productId", description = "", required = true, in = ParameterIn.PATH)
                    @PathVariable("productId")
                    String productId,
            @Parameter(hidden = true) final ServerWebExchange exchange) {
        log.info("Getting similar products for {}", productId);
        final var input = mapper.toGetSimilarProductsInput(productId);
        return useCase.getSimilarProducts(input)
                .map(mapper::toRestProductDetail)
                .as(mapper::wrapAsOkResponse);
    }
}
