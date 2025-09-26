package com.example.similarproducts.service.impl;

import com.example.similarproducts.client.ProductClient;
import com.example.similarproducts.controller.dto.ProductDetailDTO;
import com.example.similarproducts.mapper.ProductDetailMapper;
import com.example.similarproducts.service.ProductService;
import io.github.resilience4j.timelimiter.annotation.TimeLimiter;
import java.util.LinkedHashSet;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

  private final ProductClient client;
  private final ProductDetailMapper mapper;

  @Override
  @TimeLimiter(name = "products")
  public Mono<List<ProductDetailDTO>> getSimilarProducts(String productId) {
    return client.getSimilarIds(productId)
        .map(ids -> List.copyOf(new LinkedHashSet<>(ids)))
        .flatMapMany(client::getProductsInOrder)
        .map(mapper::toDTO)
        .collectList();
  }

}
