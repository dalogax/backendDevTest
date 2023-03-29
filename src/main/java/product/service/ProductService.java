package product.service;

import product.dto.ProductDetail;

import java.util.List;

public interface ProductService {
    List<ProductDetail> getProductSimilar(String productId);
}
