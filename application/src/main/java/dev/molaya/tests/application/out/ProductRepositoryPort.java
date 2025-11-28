package dev.molaya.tests.application.out;

import dev.molaya.tests.application.out.dto.ProductCommand;
import dev.molaya.tests.domain.Product;
import jakarta.validation.constraints.NotNull;
import java.util.Set;
import lombok.NonNull;
import reactor.core.publisher.Mono;

public interface ProductRepositoryPort {
    Mono<@NonNull Product> getProductDetail(@NotNull ProductCommand productCommand);

    Mono<@NonNull Set<String>> getSimilarProductIds(@NotNull ProductCommand productCommand);
}
