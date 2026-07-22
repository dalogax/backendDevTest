package com.knowmad.similarproducts.controller;

import com.knowmad.similarproducts.model.ProductDetail;
import com.knowmad.similarproducts.model.ProductNotFoundException;
import com.knowmad.similarproducts.service.SimilarProductsService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.mockito.Mockito.when;

/**
 * Tests unitarios del controlador REST {@link SimilarProductsController}.
 *
 * <p>Usa {@code @WebFluxTest} para cargar únicamente la capa web (controladores y
 * manejadores de excepción) sin levantar el contexto completo de la aplicación.
 * El servicio se mockea con {@code @MockBean} para aislar el comportamiento HTTP.</p>
 */
@WebFluxTest(SimilarProductsController.class)
@DisplayName("SimilarProductsController")
class SimilarProductsControllerTest {

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private SimilarProductsService similarProductsService;

    @Test
    @DisplayName("GET /product/{id}/similar devuelve 200 con la lista de productos similares")
    void getSimilarProducts_devuelve200ConListaDeSimilares() {
        List<ProductDetail> productos = List.of(
                new ProductDetail("2", "Dress", 19.99, true),
                new ProductDetail("3", "Blazer", 29.99, false));
        when(similarProductsService.getSimilarProducts("1")).thenReturn(Mono.just(productos));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(2);
    }

    @Test
    @DisplayName("GET /product/{id}/similar devuelve 404 cuando el producto raíz no existe")
    void getSimilarProducts_devuelve404CuandoProductoNoExiste() {
        when(similarProductsService.getSimilarProducts("999"))
                .thenReturn(Mono.error(new ProductNotFoundException("999")));

        webTestClient.get()
                .uri("/product/999/similar")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    @DisplayName("GET /product/{id}/similar devuelve 200 con lista vacía si no hay similares")
    void getSimilarProducts_devuelve200ConListaVaciaSiNoHaySimilares() {
        when(similarProductsService.getSimilarProducts("1")).thenReturn(Mono.just(List.of()));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().isOk()
                .expectBodyList(ProductDetail.class)
                .hasSize(0);
    }

    @Test
    @DisplayName("GET /product/{id}/similar devuelve 500 ante error inesperado no controlado")
    void getSimilarProducts_devuelve500AnteErrorInesperado() {
        when(similarProductsService.getSimilarProducts("1"))
                .thenReturn(Mono.error(new RuntimeException("error inesperado")));

        webTestClient.get()
                .uri("/product/1/similar")
                .exchange()
                .expectStatus().is5xxServerError();
    }
}
