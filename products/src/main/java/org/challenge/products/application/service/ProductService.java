package org.challenge.products.application.service;

import org.challenge.products.application.port.in.GetSimilarProductsUseCase;
import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.model.Product;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService implements GetSimilarProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(ProductService.class);

    private final ProductPort productPort;

    public ProductService(ProductPort productPort) {
        this.productPort = productPort;
    }

    @Override
    public List<Product> getSimilarProducts(String productId) {
        log.debug("Fetching similar products for productId: {}", productId);

        List<Product> result = productPort
                .getSimilarProductIds(productId)
                .parallelStream()
                .flatMap(id -> getProduct(id).stream())
                .toList();

        log.debug("Found {} similar products for productId: {}", result.size(), productId);
        return result;
    }

    private Optional<Product> getProduct(String productId) {
        try {
            return Optional.of(productPort.getProduct(productId));
        } catch (Exception e) {
            log.warn("Error fetching product details for id: {} - error: {}", productId, e.getMessage());
            return Optional.empty();
        }
    }

}
