package com.dev.test;

import com.dev.test.domain.aggregate.ProductDetail;
import com.dev.test.domain.aggregate.SimilarProducts;
import com.dev.test.domain.service.ProductService;
import com.dev.test.domain.usecase.ProductsUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class ProductsUseCaseImpl implements ProductsUseCase {

  private final ProductService productService;
  
  @Override
  public SimilarProducts provideSimilarProducts(String productId){
    
    final List<Integer> similarIds = productService.provideSimilarIdListById(productId);
    
    final List<ProductDetail> details = similarIds.stream()
            .map(productSimilarId -> productService.provideDetailById(productSimilarId.toString()))
            .collect(Collectors.toList());

    return SimilarProducts.builder()
            .details(details)
            .build();
  }
}
