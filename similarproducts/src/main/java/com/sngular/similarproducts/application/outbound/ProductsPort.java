package com.sngular.similarproducts.application.outbound;

import java.util.Collection;

import com.sngular.similarproducts.domain.ProductDetail;

/**
 * Port interface for accessing product data from external systems.
 */
public interface ProductsPort {

    Collection<String> getSimilarProductIds(String productId);

    ProductDetail getProduct(String productId);
}
