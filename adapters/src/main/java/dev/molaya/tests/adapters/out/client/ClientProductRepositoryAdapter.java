package dev.molaya.tests.adapters.out.client;

import dev.molaya.tests.adapters.out.client.gen.openapi.DefaultGenApi;
import dev.molaya.tests.application.out.ProductRepositoryPort;
import dev.molaya.tests.application.out.dto.ProductCommand;
import dev.molaya.tests.domain.Product;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.util.Collections;
import java.util.Set;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientProductRepositoryAdapter implements ProductRepositoryPort {
    private final DefaultGenApi defaultApi;
    private final ClientSimilarProductsMapper mapper;

    @Override
    public Mono<@NonNull Product> getProductDetail(@NotNull ProductCommand productCommand) {
        final var id = productCommand.id();
        return Mono.defer(() -> defaultApi.getProductProductId(id).map(mapper::toDomainProduct))
                .doOnError(e -> log.warn("Failed to fetch product detail for product: {}", id))
                .onErrorResume(e -> Mono.empty());
    }

    @Override
    public Mono<@NonNull Set<String>> getSimilarProductIds(@NotNull ProductCommand productCommand) {
        final var id = productCommand.id();
        return Mono.defer(() -> defaultApi.getProductSimilarids(id))
                .doOnError(e -> log.warn("Failed to fetch similar product ids for product: {}", id))
                .onErrorResume(e -> Mono.just(Collections.emptySet()));
    }
}
