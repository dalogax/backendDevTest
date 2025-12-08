package com.capitole.similarproducts.adapter.in.rest;

import static org.mockito.BDDMockito.given;

import com.capitole.similarproducts.adapter.in.rest.dto.ProductDto;
import com.capitole.similarproducts.adapter.in.rest.mapper.ProductMapper;
import com.capitole.similarproducts.domain.model.Product;
import com.capitole.similarproducts.domain.port.in.GetSimilarProductsUseCase;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

@ExtendWith(MockitoExtension.class)
class ProductControllerTest {

    private WebTestClient webTestClient;

    @Mock
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Mock
    private ProductMapper productMapper;

    @BeforeEach
    void setUp() {
        ProductController controller = new ProductController(getSimilarProductsUseCase, productMapper);
        this.webTestClient = WebTestClient.bindToController(controller).build();
    }

    @Test
    void getSimilarProducts_ShouldReturn200_WhenDataIsValid() {
        String productId = "1";
        Product p2 = new Product("2", "Product 2", BigDecimal.TEN, true);
        ProductDto d2 = new ProductDto("2", "Product 2", BigDecimal.TEN, true);
        
        given(getSimilarProductsUseCase.getSimilarProducts(productId)).willReturn(Mono.just(List.of(p2)));
        given(productMapper.toDtoList(List.of(p2))).willReturn(List.of(d2));

        webTestClient.get()
                .uri("/product/{productId}/similar", productId)
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDto.class)
                .hasSize(1);
    }
}
