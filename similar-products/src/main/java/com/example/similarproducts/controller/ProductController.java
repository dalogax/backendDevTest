package com.example.similarproducts.controller;

import com.example.similarproducts.controller.dto.ProductDetailDTO;
import com.example.similarproducts.service.ProductService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = "/product", produces = MediaType.APPLICATION_JSON_VALUE)
public class ProductController {

  private final ProductService productService;

  @GetMapping("/{productId}/similar")
  public Mono<List<ProductDetailDTO>> getSimilar(@PathVariable String productId) {
    return productService.getSimilarProducts(productId);
  }
}
