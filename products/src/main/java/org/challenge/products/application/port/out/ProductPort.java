package org.challenge.products.application.port.out;

import org.challenge.products.domain.model.Product;

import java.util.List;

public interface ProductPort {
    List<String> getSimilarProductIds(String productId);
    Product getProductDetail(String productId);
}
