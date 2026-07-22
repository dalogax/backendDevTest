package com.knowmad.similarproducts.service;

import com.knowmad.similarproducts.client.ProductClient;
import com.knowmad.similarproducts.model.ProductDetail;
import com.knowmad.similarproducts.model.ProductNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

/**
 * Tests unitarios de {@link SimilarProductsService}.
 *
 * <p>Se mockea {@link ProductClient} para aislar la lógica de negocio del servicio
 * sin depender de llamadas HTTP reales. Se usa {@link StepVerifier} de Reactor Test
 * para verificar el comportamiento de los flujos reactivos.</p>
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("SimilarProductsService")
class SimilarProductsServiceTest {

    @Mock
    private ProductClient productClient;

    @InjectMocks
    private SimilarProductsService service;

    @Test
    @DisplayName("devuelve la lista ordenada cuando todos los productos responden correctamente")
    void getSimilarProducts_devuelveListaOrdenadaCuandoTodoVaBien() {
        ProductDetail dress = new ProductDetail("2", "Dress", 19.99, true);
        ProductDetail blazer = new ProductDetail("3", "Blazer", 29.99, false);

        when(productClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3")));
        when(productClient.getProductDetail("2")).thenReturn(Mono.just(dress));
        when(productClient.getProductDetail("3")).thenReturn(Mono.just(blazer));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(list -> {
                    assertThat(list).hasSize(2);
                    assertThat(list.get(0).id()).isEqualTo("2");
                    assertThat(list.get(1).id()).isEqualTo("3");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("omite los productos que devuelven error o timeout sin romper la respuesta")
    void getSimilarProducts_omiteProductosConError() {
        ProductDetail dress = new ProductDetail("2", "Dress", 19.99, true);

        when(productClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3")));
        when(productClient.getProductDetail("2")).thenReturn(Mono.just(dress));
        when(productClient.getProductDetail("3")).thenReturn(Mono.empty()); // simula 404/500/timeout

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(list -> {
                    assertThat(list).hasSize(1);
                    assertThat(list.get(0).id()).isEqualTo("2");
                })
                .verifyComplete();
    }

    @Test
    @DisplayName("propaga ProductNotFoundException cuando el producto raíz no existe")
    void getSimilarProducts_propagaProductoNotFoundException() {
        when(productClient.getSimilarIds("999"))
                .thenReturn(Mono.error(new ProductNotFoundException("999")));

        StepVerifier.create(service.getSimilarProducts("999"))
                .expectError(ProductNotFoundException.class)
                .verify();
    }

    @Test
    @DisplayName("devuelve lista vacía cuando el producto no tiene similares")
    void getSimilarProducts_devuelveListaVaciaSiNoHaySimilares() {
        when(productClient.getSimilarIds("1")).thenReturn(Mono.just(List.of()));

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(list -> assertThat(list).isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("devuelve lista vacía cuando todos los productos similares fallan")
    void getSimilarProducts_devuelveListaVaciaCuandoTodosLosProductosFallan() {
        when(productClient.getSimilarIds("1")).thenReturn(Mono.just(List.of("2", "3")));
        when(productClient.getProductDetail("2")).thenReturn(Mono.empty());
        when(productClient.getProductDetail("3")).thenReturn(Mono.empty());

        StepVerifier.create(service.getSimilarProducts("1"))
                .assertNext(list -> assertThat(list).isEmpty())
                .verifyComplete();
    }
}
