package dev.molaya.tests.application.service;

import dev.molaya.tests.application.config.SimilarProductsConfigurationProperties;
import dev.molaya.tests.application.in.GetSimilarProductsUseCase;
import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.application.out.ProductRepositoryPort;
import dev.molaya.tests.application.out.dto.ProductCommand;
import dev.molaya.tests.domain.Product;
import dev.molaya.tests.domain.exceptions.IntegrationException;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarProductsService implements GetSimilarProductsUseCase {
    private final ProductRepositoryPort port;
    private final ServiceSimilarProductsMapper mapper;
    private final SimilarProductsConfigurationProperties properties;

    @Override
    public Flux<@NonNull Product> getSimilarProducts(@NotNull GetSimilarProductsInput input) {
        final var command = mapper.toProductCommandSafe(input);
        return Flux.defer(() -> getProductCommandsForIdsSafe(command)
                        .flatMap(port::getProductDetail, properties.parallelRequests()))
                .doOnError(e -> log.error("Error fetching similar products for product: {}", input.productId(), e))
                .onErrorMap(e -> new IntegrationException("Failed to fetch product details", e));
    }

    private Flux<ProductCommand> getProductCommandsForIdsSafe(ProductCommand command) {
        return Mono.defer(() -> port.getSimilarProductIds(command))
                .flatMapMany(Flux::fromIterable)
                .take(properties.limit())
                .map(ProductCommand::new);
    }
}
