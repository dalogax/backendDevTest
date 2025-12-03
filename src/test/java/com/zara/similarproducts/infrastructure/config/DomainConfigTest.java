package com.zara.similarproducts.infrastructure.config;

import com.zara.similarproducts.application.port.out.ProductRepository;
import com.zara.similarproducts.application.port.out.SimilarProductsRepository;
import com.zara.similarproducts.domain.service.SimilarProductsDomainService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class DomainConfigTest {

    @Mock
    private SimilarProductsRepository similarProductsRepository;
    
    @Mock
    private ProductRepository productRepository;

    @Test
    void shouldCreateSimilarProductsDomainService() {
        // Given
        DomainConfig config = new DomainConfig();
        
        // When
        SimilarProductsDomainService service = config.similarProductsDomainService(
                similarProductsRepository, productRepository);
        
        // Then
        assertThat(service).isNotNull();
    }
}