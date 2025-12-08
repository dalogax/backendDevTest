package com.capitole.similarproducts.domain.port.in;

import com.capitole.similarproducts.domain.model.Product;
import java.util.List;
import org.jspecify.annotations.NonNull;
import reactor.core.publisher.Mono;

/**
 * Input port (use case) defining the contract for retrieving similar products.
 */
public interface GetSimilarProductsUseCase {

    /**
     * Retrieves a list of similar products for a given product ID.
     *
     * @param productId the ID of the product to find similar products for
     * @return Mono containing list of similar products with their details
     */
    Mono<@NonNull List<Product>> getSimilarProducts(String productId);
}
