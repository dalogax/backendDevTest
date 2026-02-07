package com.globant.interview.david.msdavidmobilephone.infrastructure.output;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.repository.ProductRepository;
import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.FeignProductClient;
import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto.ProductDetailResponse;
import com.globant.interview.david.msdavidmobilephone.infrastructure.output.client.dto.SimilarIdsResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
@RequiredArgsConstructor
@Slf4j
public class FeignProductRepository implements ProductRepository {

    private final FeignProductClient feignProductClient;

    @Override
    public List<String> getSimilarProductIds(String productId) {
        log.info("Fetching similar product ids for productId: {}", productId);
        SimilarIdsResponse response = feignProductClient.getSimilarIds(productId);
        return response.ids();
    }

    @Override
    public Optional<Product> getProductDetail(String productId) {
        log.info("Fetching product detail for productId: {}", productId);
        try {
            ProductDetailResponse response = feignProductClient.getProductDetail(productId);
            Product product = new Product(
                response.id(),
                response.name(),
                response.price(),
                response.availability()
            );
            return Optional.of(product);
        } catch (Exception e) {
            log.warn("Product not found for productId: {}", productId);
            return Optional.empty();
        }
    }
}
