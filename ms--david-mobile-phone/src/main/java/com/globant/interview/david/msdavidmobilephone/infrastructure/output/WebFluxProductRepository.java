package com.globant.interview.david.msdavidmobilephone.infrastructure.output;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.repository.ProductRepository;
import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.WebFluxProductClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Repository;

import java.time.Duration;
import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
@ConditionalOnProperty(name = "product.repository.impl", havingValue = "webflux", matchIfMissing = false)
public class WebFluxProductRepository implements ProductRepository {

    private final WebFluxProductClient webFluxProductClient;

    @Override
    public List<String> getSimilarProductIds(String productId) {
        log.info("Fetching similar product ids for productId: {}", productId);
        try {
            List<String> ids = webFluxProductClient.getSimilarIds(productId)
                    .collectList()
                    .block(Duration.ofSeconds(3));
            return ids != null ? ids : List.of();
        } catch (Exception e) {
            log.warn("Error fetching similar ids for productId: {}", productId, e);
            return List.of();
        }
    }

    @Override
    public Optional<Product> getProductDetail(String productId) {
        log.info("Fetching product detail for productId: {}", productId);
        try {
            return webFluxProductClient.getProductDetail(productId)
                    .map(response -> new Product(
                            response.id(),
                            response.name(),
                            response.price(),
                            response.availability()
                    ))
                    .blockOptional(Duration.ofSeconds(3));
        } catch (Exception e) {
            log.warn("Product not found for productId: {}", productId);
            return Optional.empty();
        }
    }
}
