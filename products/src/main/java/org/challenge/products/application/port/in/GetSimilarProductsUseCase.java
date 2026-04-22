package org.challenge.products.application.port.in;

import org.challenge.products.domain.model.Product;

import java.util.List;

public interface GetSimilarProductsUseCase {
    List<Product> getSimilarProducts(String productId);
}
