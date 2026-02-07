package com.globant.interview.david.msdavidmobilephone.application.usecase;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.model.SimilarProducts;
import com.globant.interview.david.msdavidmobilephone.domain.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetSimilarProductsUseCaseTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private GetSimilarProductsUseCase getSimilarProductsUseCase;

    @Test
    void shouldReturnSimilarProducts() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");

        Product p2 = new Product("2", "Samsung", new BigDecimal("899"), true);
        Product p3 = new Product("3", "iPhone", new BigDecimal("999"), true);
        Product p4 = new Product("4", "Google", new BigDecimal("799"), true);

        when(productRepository.getSimilarProductIds(productId)).thenReturn(similarIds);
        when(productRepository.getProductDetail("2")).thenReturn(Optional.of(p2));
        when(productRepository.getProductDetail("3")).thenReturn(Optional.of(p3));
        when(productRepository.getProductDetail("4")).thenReturn(Optional.of(p4));

        SimilarProducts result = getSimilarProductsUseCase.execute(productId);

        assertEquals(3, result.products().size());
        verify(productRepository, times(1)).getSimilarProductIds(productId);
        verify(productRepository, times(1)).getProductDetail("2");
        verify(productRepository, times(1)).getProductDetail("3");
        verify(productRepository, times(1)).getProductDetail("4");
    }

    @Test
    void shouldReturnEmptyWhenNoSimilarIds() {
        String productId = "1";

        when(productRepository.getSimilarProductIds(productId)).thenReturn(List.of());

        SimilarProducts result = getSimilarProductsUseCase.execute(productId);

        assertTrue(result.products().isEmpty());
        verify(productRepository, never()).getProductDetail(anyString());
    }

    @Test
    void shouldFilterOutNotFoundProducts() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3", "4");

        Product p2 = new Product("2", "Samsung", new BigDecimal("899"), true);
        Product p4 = new Product("4", "Google", new BigDecimal("799"), true);

        when(productRepository.getSimilarProductIds(productId)).thenReturn(similarIds);
        when(productRepository.getProductDetail("2")).thenReturn(Optional.of(p2));
        when(productRepository.getProductDetail("3")).thenReturn(Optional.empty());
        when(productRepository.getProductDetail("4")).thenReturn(Optional.of(p4));

        SimilarProducts result = getSimilarProductsUseCase.execute(productId);

        assertEquals(2, result.products().size());
        assertTrue(result.products().stream().allMatch(p -> List.of("2", "4").contains(p.id())));
    }

    @Test
    void shouldCallRepositoryForSimilarIds() {
        String productId = "1";
        List<String> similarIds = List.of("2", "3");

        when(productRepository.getSimilarProductIds(productId)).thenReturn(similarIds);
        when(productRepository.getProductDetail(anyString())).thenReturn(Optional.empty());

        getSimilarProductsUseCase.execute(productId);

        verify(productRepository, times(1)).getSimilarProductIds(productId);
    }
}
