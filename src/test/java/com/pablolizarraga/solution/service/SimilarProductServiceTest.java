package com.pablolizarraga.solution.service;

import com.pablolizarraga.solution.application.exception.NotFoundException;
import com.pablolizarraga.solution.application.port.out.SimilarProductRepositoryPort;
import com.pablolizarraga.solution.application.service.SimilarProductService;
import com.pablolizarraga.solution.domain.ProductDetail;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SimilarProductServiceTest {

    @Mock
    private SimilarProductRepositoryPort similarProductRepositoryPort;

    @InjectMocks
    private SimilarProductService similarProductService;

    @Test
    void givenProductId_whenGetSimilar_thenReturnProductDetailsList() throws Exception {
        ProductDetail product1 = new ProductDetail("1","Product1", BigDecimal.TEN,true);
        ProductDetail product2 = new ProductDetail("2","Product2", BigDecimal.ONE,true);
        ProductDetail product3 = new ProductDetail("3","Product3", BigDecimal.ZERO,true);

        given(similarProductRepositoryPort.getProduct("1"))
                .willReturn(Optional.of(product1));

        given(similarProductRepositoryPort.getProduct("2"))
                .willReturn(Optional.of(product2));

        given(similarProductRepositoryPort.getProduct("3"))
                .willReturn(Optional.of(product3));

        given(similarProductRepositoryPort.getSimilarProductIds("1"))
                .willReturn(List.of("2","3"));

        Assertions.assertThat(similarProductService.getSimilarProducts("1"))
                .filteredOn(productDetail -> productDetail.getId() != null)
                .filteredOn(productDetail -> productDetail.getName().startsWith("Product"))
                .filteredOn(productDetail -> productDetail.getPrice() != null)
                .isNotNull()
                .isNotEmpty()
                .hasSize(2);
    }

    @Test
    void givenNonExistingProductId_whenGetSimilar_thenNotFoundError() throws Exception {

        given(similarProductRepositoryPort.getProduct("1"))
                .willReturn(Optional.empty());

        assertThatThrownBy(() -> {
            similarProductService.getSimilarProducts("1");
        }).isInstanceOf(NotFoundException.class);
    }
}
