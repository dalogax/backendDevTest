package com.dev.test.service.implementation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;

import com.dev.test.domain.aggregate.ProductDetail;
import com.dev.test.domain.exceptions.InternalErrorException;
import com.dev.test.domain.exceptions.NotFoundException;
import com.dev.test.domain.mapper.ProductMapper;
import com.dev.test.mock.MockClient;
import com.dev.test.mock.response.MockClientResponse;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;

@ExtendWith(MockitoExtension.class)
public class ProductServiceImplTest {
  @Mock
  private MockClient mockClient;
  @Mock
  private ProductMapper productMapper;
  private final static String PRODUCT_ID ="1";
  
  @InjectMocks
  private ProductServiceImpl productService;
  @Nested
  class ProvideDetailById{
    
    @Test
    void when_callMockClientHasResults_then_returnProductDetail(){
      final MockClientResponse mockClientResponse = new MockClientResponse();
      mockClientResponse.setId("10");
      mockClientResponse.setName("name");
      mockClientResponse.setPrice(100);
      mockClientResponse.setAvailability(true);
      final ProductDetail detail=ProductDetail.builder()
          .id("10")
          .name("name")
          .price(100)
          .availability(true)
          .build();
      when(mockClient.findProductDetail(PRODUCT_ID)).thenReturn(mockClientResponse);
      when(productMapper.asProductDetail(mockClientResponse)).thenReturn(detail);
      final ProductDetail result = productService.provideDetailById(PRODUCT_ID);
      assertThat(result).isEqualTo(detail);
      verify(mockClient).findProductDetail(PRODUCT_ID);
      verify(productMapper).asProductDetail(mockClientResponse);
    }

    @Test
    void when_callMockClientHasNotResults_then_throwNotFoundException(){
      when(mockClient.findProductDetail(PRODUCT_ID)).thenReturn(null);
      
      final NotFoundException result = assertThrows(NotFoundException.class,
          () -> productService.provideDetailById(PRODUCT_ID));

      assertThat(result.getMessage()).isEqualTo(
          String.format("Can not found detail of id: %s", PRODUCT_ID));
      verify(productMapper, never()).asProductDetail(any(MockClientResponse.class));
    }

    @Test
    void when_callMockClientReturnHttpClientErrorException_then_throwNotFoundException(){
      when(mockClient.findProductDetail(PRODUCT_ID)).thenThrow(HttpClientErrorException.class);

      final NotFoundException result = assertThrows(NotFoundException.class,
          () -> productService.provideDetailById(PRODUCT_ID));

      assertThat(result.getMessage()).isEqualTo(
          String.format("Can not found detail of id: %s", PRODUCT_ID));
      verify(productMapper, never()).asProductDetail(any(MockClientResponse.class));
    }
    
    @Test
    void when_callMockClientReturnResourceAccessException_then_throwNotFoundException(){
      when(mockClient.findProductDetail(PRODUCT_ID)).thenThrow(ResourceAccessException.class);

      final InternalErrorException result = assertThrows(InternalErrorException.class,
          () -> productService.provideDetailById(PRODUCT_ID));

      assertThat(result.getMessage()).isEqualTo("Cannot access the mock client, please try again later or contact the administrator.");
      verify(productMapper, never()).asProductDetail(any(MockClientResponse.class));
    }
  }
  @Nested
  class ProvideSimilarIdListById{
    @Test
    void when_callMockClientHasResults_then_returnListOfId(){
      final List<Integer> expectedResult = List.of(1,2,3,4);
      when(mockClient.findSimilarIds(PRODUCT_ID)).thenReturn(expectedResult);
      
      final List<Integer> result = productService.provideSimilarIdListById(PRODUCT_ID);
      assertThat(result).isEqualTo(expectedResult);
    }

    @NullAndEmptySource
    @ParameterizedTest
    void when_callMockClientHasNotResults_then_throwNotFoundException(final List<Integer> similarIds){
      when(mockClient.findSimilarIds(PRODUCT_ID)).thenReturn(similarIds);

      final NotFoundException result = assertThrows(NotFoundException.class,
          () -> productService.provideSimilarIdListById(PRODUCT_ID));

      assertThat(result.getMessage()).isEqualTo(
          String.format("Can not found similar ids of %s", PRODUCT_ID));
    }

    @Test
    void when_callMockClientReturnHttpClientErrorException_then_throwNotFoundException(){
      when(mockClient.findSimilarIds(PRODUCT_ID)).thenThrow(HttpClientErrorException.class);

      final NotFoundException result = assertThrows(NotFoundException.class,
          () -> productService.provideSimilarIdListById(PRODUCT_ID));

      assertThat(result.getMessage()).isEqualTo(
          String.format("Can not found similar ids of %s", PRODUCT_ID));
    }

    @Test
    void when_callMockClientReturnResourceAccessException_then_throwNotFoundException(){
      when(mockClient.findSimilarIds(PRODUCT_ID)).thenThrow(ResourceAccessException.class);

      final InternalErrorException result = assertThrows(InternalErrorException.class,
          () -> productService.provideSimilarIdListById(PRODUCT_ID));

      assertThat(result.getMessage()).isEqualTo("Cannot access the mock client, please try again later or contact the administrator.");
    }
  }
}