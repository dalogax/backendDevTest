package dev.molaya.tests.application.in;

import dev.molaya.tests.application.in.dto.GetSimilarProductsInput;
import dev.molaya.tests.domain.Product;
import jakarta.validation.constraints.NotNull;
import lombok.NonNull;
import reactor.core.publisher.Flux;

@FunctionalInterface
public interface GetSimilarProductsUseCase {
    Flux<@NonNull Product> getSimilarProducts(@NotNull GetSimilarProductsInput input);
}
