package org.challenge.products.application.service;

import org.challenge.products.application.port.in.GetSimilarProductsUseCase;
import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.model.Product;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductService implements GetSimilarProductsUseCase {

    private final ProductPort productPort;

    public ProductService(ProductPort productPort) {
        this.productPort = productPort;
    }

    @Override
    public List<Product> getSimilarProducts(String productId) {
        return productPort
                .getSimilarProductIds(productId)
                .parallelStream()
                .map(productPort::getProductDetail)
                .toList();
    }
}
