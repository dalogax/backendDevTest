package com.dev.test.controller;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.test.domain.aggregate.ProductDetail;
import com.dev.test.domain.aggregate.SimilarProducts;
import com.dev.test.domain.exceptions.InternalErrorException;
import com.dev.test.domain.exceptions.NotFoundException;
import com.dev.test.domain.usecase.ProductsUseCase;
import com.dev.test.mapper.SimilarProductsMapper;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
public class ProductsControllerTest {
  @Mock
  private ProductsUseCase productsUseCase;
  @Mock
  private SimilarProductsMapper similarProductsMapper;
  @InjectMocks
  private ProductsController productsController;
  final private static String PRODUCT_ID = "1";
  @Nested
  class GetProductsById{
    @Test
    void when_getProductsByIdCallIsSuccessful_then_returnOKResponseEntity(){
      final SimilarProducts similarProducts = SimilarProducts.builder()
          .details(List.of(ProductDetail.builder()
                  .id(PRODUCT_ID)
                  .name("name")
                  .price(100)
                  .availability(true)
              .build()))
          .build();
      
      when(productsUseCase.provideSimilarProducts(PRODUCT_ID)).thenReturn(similarProducts);
      productsController.getProductsById(PRODUCT_ID);
      
      verify(similarProductsMapper).asSimilarProductsDTO(similarProducts);
    }
  }
  @Nested
  class HandleInternalErrorException{
    @Test
    void when_getHandleInternalErrorException_then_returnInternalServerErrorResponseEntity(){
      final ResponseEntity<String> result = productsController.handleInternalErrorException(new InternalErrorException("ERROR TEST"));
      assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, result.getStatusCode());
      assertNotNull(result.getBody());
    }
  }
  @Nested
  class HandleNotFoundException{
    @Test
    void when_getHandleNotFoundException_then_returnHandleNotFoundExceptionResponseEntity(){
      final ResponseEntity<String> result = productsController.handleNotFoundException(new NotFoundException("ERROR TEST"));
      assertEquals(HttpStatus.NOT_FOUND, result.getStatusCode());
      assertNotNull(result.getBody());
    }
  }
  
}