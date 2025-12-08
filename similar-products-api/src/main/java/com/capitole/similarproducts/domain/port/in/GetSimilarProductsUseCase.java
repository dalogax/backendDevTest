package com.capitole.similarproducts.domain.port.in;

import com.capitole.similarproducts.domain.model.Product;
import java.util.List;

/**
 * Input port (use case) defining the contract for retrieving similar products.
 */
public interface GetSimilarProductsUseCase {

    /**
     * Retrieves a list of similar products for a given product ID.
     *
     * @param productId the ID of the product to find similar products for
     * @return List of similar products with their details
     */
    List<Product> getSimilarProducts(String productId);
}
