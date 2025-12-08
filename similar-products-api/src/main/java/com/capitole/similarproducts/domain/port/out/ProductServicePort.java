package com.capitole.similarproducts.domain.port.out;

import com.capitole.similarproducts.domain.model.Product;
import java.util.List;

/**
 * Output port defining the contract for external product service interactions
 */
public interface ProductServicePort {

    /**
     * Retrieves the list of similar product IDs for a given product
     *
     * @param productId the ID of the product
     * @return List of similar product IDs, or empty if not found
     */
    List<String> getSimilarProductIds(String productId);

    /**
     * Retrieves detailed information for a specific product
     *
     * @param productId the ID of the product
     * @return Product details
     */
    Product getProductDetail(String productId);
}
