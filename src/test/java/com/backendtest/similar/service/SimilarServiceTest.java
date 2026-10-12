package com.backendtest.similar.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.when;

import com.backendtest.similar.dto.ProductDetail;
import com.backendtest.similar.exception.ProductNotFoundException;
import com.backendtest.similar.port.ProductCatalogPort;
import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SimilarServiceTest {

    @Mock
    ProductCatalogPort catalog;

    private SimilarService service(long timeoutMs) {
        return new SimilarService(catalog, timeoutMs, Executors.newVirtualThreadPerTaskExecutor());
    }

    private static ProductDetail detail(String id) {
        return new ProductDetail(id, "n-" + id, new BigDecimal("9.99"), true);
    }

    @Test
    void filters404And500PreservingOrder() {
        when(catalog.getSimilarIds("4")).thenReturn(List.of("1", "2", "5"));
        when(catalog.getProduct("1")).thenReturn(Optional.of(detail("1")));
        when(catalog.getProduct("2")).thenReturn(Optional.of(detail("2")));
        when(catalog.getProduct("5")).thenReturn(Optional.empty());

        assertThat(service(2000).getSimilarProducts("4"))
                .extracting(ProductDetail::id)
                .containsExactly("1", "2");

        when(catalog.getSimilarIds("5")).thenReturn(List.of("1", "2", "6"));
        when(catalog.getProduct("6")).thenReturn(Optional.empty());

        assertThat(service(2000).getSimilarProducts("5"))
                .extracting(ProductDetail::id)
                .containsExactly("1", "2");
    }

    @Test
    void slowDetailBeyondTimeoutIsFiltered() {
        when(catalog.getSimilarIds("2")).thenReturn(List.of("3", "100"));
        when(catalog.getProduct("3")).thenReturn(Optional.of(detail("3")));
        when(catalog.getProduct("100")).thenAnswer(inv -> {
            Thread.sleep(1000);
            return Optional.of(detail("100"));
        });

        long start = System.currentTimeMillis();
        List<ProductDetail> result = service(200).getSimilarProducts("2");
        long elapsed = System.currentTimeMillis() - start;

        assertThat(result).extracting(ProductDetail::id).containsExactly("3");
        assertThat(elapsed).isLessThan(2000);
    }

    @Test
    void propagatesCollection404AndEmptyList() {
        when(catalog.getSimilarIds("999")).thenThrow(new ProductNotFoundException("999"));
        assertThatThrownBy(() -> service(2000).getSimilarProducts("999")).isInstanceOf(ProductNotFoundException.class);

        when(catalog.getSimilarIds("1")).thenReturn(List.of());
        assertThat(service(2000).getSimilarProducts("1")).isEmpty();
    }

    @Test
    void dedupesPreservingFirstOrder() {
        when(catalog.getSimilarIds("1")).thenReturn(List.of("2", "2", "3"));
        when(catalog.getProduct("2")).thenReturn(Optional.of(detail("2")));
        when(catalog.getProduct("3")).thenReturn(Optional.of(detail("3")));

        assertThat(service(2000).getSimilarProducts("1"))
                .extracting(ProductDetail::id)
                .containsExactly("2", "3");
    }

    @Test
    void rejectsBlankProductId() {
        assertThatThrownBy(() -> service(2000).getSimilarProducts(" ")).isInstanceOf(IllegalArgumentException.class);
    }
}
