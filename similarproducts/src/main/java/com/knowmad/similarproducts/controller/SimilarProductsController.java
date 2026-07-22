package com.knowmad.similarproducts.controller;

import com.knowmad.similarproducts.model.ProductDetail;
import com.knowmad.similarproducts.model.ProductNotFoundException;
import com.knowmad.similarproducts.service.SimilarProductsService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Controlador REST que expone el endpoint de productos similares.
 *
 * <p>Implementa el contrato definido en {@code similarProducts.yaml}:
 * {@code GET /product/{productId}/similar}.</p>
 *
 * <p>Este controlador es reactivo (Spring WebFlux): no bloquea ningún hilo
 * mientras espera respuestas del servicio externo, lo que permite gestionar
 * un alto número de peticiones concurrentes con muy pocos recursos.</p>
 */
@RestController
public class SimilarProductsController {

    private final SimilarProductsService similarProductsService;

    /**
     * Constructor con inyección de dependencias del servicio de negocio.
     *
     * @param similarProductsService servicio que orquesta la lógica de productos similares
     */
    public SimilarProductsController(SimilarProductsService similarProductsService) {
        this.similarProductsService = similarProductsService;
    }

    /**
     * Devuelve la lista de productos similares al producto indicado.
     *
     * <p>Endpoint: {@code GET /product/{productId}/similar}</p>
     *
     * <p>Respuestas posibles según el contrato {@code similarProducts.yaml}:</p>
     * <ul>
     *   <li><b>200 OK</b>: lista de {@link ProductDetail} ordenada por similitud.
     *       Puede ser una lista vacía si todos los productos similares fallaron
     *       (404, 5xx o timeout en el servicio externo).</li>
     *   <li><b>404 Not Found</b>: el producto raíz ({@code productId}) no existe
     *       en el catálogo (el servicio externo devolvió 404 al consultar sus similares).</li>
     * </ul>
     *
     * @param productId identificador del producto del que se quieren obtener similares
     * @return {@code Mono} con el {@link ResponseEntity} apropiado:
     *         200 con la lista de productos, o 404 si el producto no existe
     */
    @GetMapping("/product/{productId}/similar")
    public Mono<ResponseEntity<List<ProductDetail>>> getSimilarProducts(
            @PathVariable String productId) {
        return similarProductsService.getSimilarProducts(productId)
                // Si el servicio completa correctamente, envolvemos la lista en un 200 OK.
                .map(ResponseEntity::ok)
                // Si el servicio lanza ProductNotFoundException (producto raíz no encontrado),
                // devolvemos 404. El resto de errores se dejan escalar como 5xx.
                .onErrorResume(
                        ProductNotFoundException.class,
                        e -> Mono.just(ResponseEntity.notFound().build()));
    }
}
