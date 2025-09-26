package com.example.similarproducts.service;

import static org.mockito.Mockito.when;

import com.example.similarproducts.client.ProductClient;
import com.example.similarproducts.controller.dto.ProductDetailDTO;
import com.example.similarproducts.mapper.ProductDetailMapper;
import com.example.similarproducts.model.ProductDetail;
import com.example.similarproducts.service.impl.ProductServiceImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

@ExtendWith(MockitoExtension.class)
class ProductServiceImplTest {

  @Mock
  private ProductClient client;

  @Mock
  private ProductDetailMapper mapper;

  private ProductService service;

  @BeforeEach
  void setUp() {
    service = new ProductServiceImpl(client, mapper);
  }

  @Test
  void getSimilarProducts_returnsDetails() {
    List<String> ids = List.of("2", "3");

    ProductDetail detail1 = new ProductDetail();
    detail1.setId("2");
    detail1.setName("Test Product 2");
    detail1.setPrice(20.0);
    detail1.setAvailability(true);

    ProductDetail detail2 = new ProductDetail();
    detail2.setId("3");
    detail2.setName("Test Product 3");
    detail2.setPrice(30.0);
    detail2.setAvailability(false);

    ProductDetailDTO dto1 = new ProductDetailDTO("2", "Test Product 2", 20.0, true);
    ProductDetailDTO dto2 = new ProductDetailDTO("3", "Test Product 3", 30.0, false);

    when(client.getSimilarIds("1")).thenReturn(Mono.just(ids));
    when(client.getProductsInOrder(ids)).thenReturn(Flux.just(detail1, detail2));
    when(mapper.toDTO(detail1)).thenReturn(dto1);
    when(mapper.toDTO(detail2)).thenReturn(dto2);

    StepVerifier.create(service.getSimilarProducts("1"))
        .expectNext(List.of(dto1, dto2))
        .verifyComplete();
  }

  @Test
  void getSimilarProducts_emptyWhenNoSimilarIds() {
    when(client.getSimilarIds("99")).thenReturn(Mono.just(List.of()));
    when(client.getProductsInOrder(List.of())).thenReturn(Flux.empty());

    StepVerifier.create(service.getSimilarProducts("99"))
        .expectNext(List.of())
        .verifyComplete();
  }

  @Test
  void getSimilarProducts_removesDuplicatesPreservingOrder() {
    List<String> idsWithDupes = List.of("2", "3", "2");

    ProductDetail detail1a = new ProductDetail();
    detail1a.setId("2");
    detail1a.setName("P2");
    detail1a.setPrice(20.0);
    detail1a.setAvailability(true);

    ProductDetail detail2 = new ProductDetail();
    detail2.setId("3");
    detail2.setName("P3");
    detail2.setPrice(30.0);
    detail2.setAvailability(false);

    when(client.getSimilarIds("1")).thenReturn(Mono.just(idsWithDupes));
    when(client.getProductsInOrder(List.of("2", "3"))).thenReturn(Flux.just(detail1a, detail2));

    ProductDetailDTO dto1 = new ProductDetailDTO("2", "P2", 20.0, true);
    ProductDetailDTO dto2 = new ProductDetailDTO("3", "P3", 30.0, false);
    when(mapper.toDTO(detail1a)).thenReturn(dto1);
    when(mapper.toDTO(detail2)).thenReturn(dto2);

    StepVerifier.create(service.getSimilarProducts("1"))
        .expectNext(List.of(dto1, dto2))
        .verifyComplete();
  }
}
