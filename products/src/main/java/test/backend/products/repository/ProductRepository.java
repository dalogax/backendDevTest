package test.backend.products.repository;

import test.backend.products.entity.ProductDetail;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    Optional<List<String>> getSimilarProductsById(String productId);
    Optional<ProductDetail> getProductDetailById(String productId);

}
