package com.pablolizarraga.solution.application.port.in;

import com.pablolizarraga.solution.domain.ProductDetail;

import java.util.List;

public interface SimilarProductServicePort {

    List<ProductDetail> getSimilarProducts(String productId);
}
