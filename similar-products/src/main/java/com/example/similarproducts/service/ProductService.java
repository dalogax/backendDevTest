package com.example.similarproducts.service;

import com.example.similarproducts.controller.dto.ProductDetailDTO;
import java.util.List;
import reactor.core.publisher.Mono;

public interface ProductService {

  Mono<List<ProductDetailDTO>> getSimilarProducts(String productId);

}
