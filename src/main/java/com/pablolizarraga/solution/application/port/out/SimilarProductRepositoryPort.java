package com.pablolizarraga.solution.application.port.out;


import com.pablolizarraga.solution.domain.ProductDetail;

import java.util.List;
import java.util.Optional;

public interface SimilarProductRepositoryPort {

    List<String> getSimilarProductIds(String productId);
    Optional<ProductDetail> getProduct(String productId);
}
