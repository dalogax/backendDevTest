package com.dev.test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.test.domain.aggregate.ProductDetail;
import com.dev.test.domain.aggregate.SimilarProducts;
import com.dev.test.domain.service.ProductService;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class ProductsUseCaseImplTest {
  @Mock
  private ProductService productService;
  @InjectMocks
  private ProductsUseCaseImpl productsUseCase;
  final private static String PRODUCT_ID = "1";
  @Nested
  class ProvideSimilarProducts{
    @Test
    void when_provideSimilarProductsCallServices_then_returnCorrectResponses(){
      final List<Integer> similarIds = List.of(1,2,3,4);
      final ProductDetail detail = ProductDetail.builder()
          .id("10")
          .name("name")
          .price(100)
          .availability(true)
          .build();
      when(productService.provideSimilarIdListById(PRODUCT_ID)).thenReturn(similarIds);
      when(productService.provideDetailById(anyString())).thenReturn(detail);

      final SimilarProducts result = productsUseCase.provideSimilarProducts(PRODUCT_ID);
      final SimilarProducts expectedResult = SimilarProducts.builder().details(List.of(detail,detail,detail,detail)).build();
      
      assertThat(result).isEqualTo(expectedResult);
      verify(productService, times(4)).provideDetailById(anyString());
    }
  }
}