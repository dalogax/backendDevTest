package com.backendtest.similar.application.port.in;

import com.backendtest.similar.dto.ProductDetail;
import java.util.List;

public interface SimilarProductsUseCase {

    List<ProductDetail> getSimilarProducts(String productId);
}
