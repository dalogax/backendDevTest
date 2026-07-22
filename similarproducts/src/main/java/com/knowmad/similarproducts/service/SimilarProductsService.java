package com.knowmad.similarproducts.service;

import com.knowmad.similarproducts.client.ProductClient;
import com.knowmad.similarproducts.model.ProductDetail;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * Servicio que orquesta la lógica de negocio para obtener los productos similares.
 *
 * <p>Actúa como capa intermedia entre el controlador REST y el cliente HTTP externo.
 * Su responsabilidad es coordinar las llamadas al API externo de forma reactiva
 * y eficiente, delegando en {@link ProductClient} los detalles de comunicación.</p>
 */
@Service
public class SimilarProductsService {

    private final ProductClient productClient;

    /**
     * Constructor con inyección de dependencias del cliente HTTP.
     *
     * @param productClient cliente reactivo para llamar al servicio externo de productos
     */
    public SimilarProductsService(ProductClient productClient) {
        this.productClient = productClient;
    }

    /**
     * Obtiene los detalles de los productos similares a uno dado.
     *
     * <p>El flujo de ejecución es el siguiente:</p>
     * <ol>
     *   <li>Se consultan los IDs de productos similares al servicio externo.</li>
     *   <li>Se convierte la lista de IDs en un {@code Flux} para procesarlos como stream.</li>
     *   <li>Por cada ID se lanza una petición de detalle de producto de forma concurrente
     *       usando {@code flatMapSequential}: todas las peticiones HTTP se ejecutan en
     *       paralelo pero los resultados se emiten en el mismo orden que los IDs originales,
     *       preservando la ordenación por similitud.</li>
     *   <li>Los productos que fallen (404, 5xx, timeout) son ignorados silenciosamente
     *       porque {@link ProductClient#getProductDetail} devuelve {@code Mono.empty()}
     *       en caso de error, y {@code flatMapSequential} omite los vacíos.</li>
     *   <li>Se recogen todos los resultados en una lista y se emite como {@code Mono}.</li>
     * </ol>
     *
     * <p><b>Nota sobre rendimiento:</b> {@code flatMapSequential} lanza hasta 256 peticiones
     * concurrentes por defecto (configurable), lo que lo hace mucho más eficiente que
     * un bucle secuencial cuando hay múltiples IDs similares.</p>
     *
     * @param productId identificador del producto del que se quieren obtener los similares
     * @return {@code Mono} que emite la lista de {@link ProductDetail} de los productos
     *         similares disponibles (puede ser vacía si ninguno responde correctamente),
     *         o completa con {@link ProductNotFoundException} si el producto raíz no existe
     */
    public Mono<List<ProductDetail>> getSimilarProducts(String productId) {
        return productClient.getSimilarIds(productId)
                // Convertimos la lista de IDs en un Flux para poder operar sobre
                // cada elemento de forma reactiva y concurrente.
                .flatMapMany(Flux::fromIterable)
                // flatMapSequential: lanza todas las llamadas HTTP en paralelo
                // pero mantiene el orden de emisión igual al de los IDs recibidos.
                // Los Mono.empty() devueltos por errores se descartan automáticamente.
                .flatMapSequential(productClient::getProductDetail)
                // Recogemos todos los resultados en una lista para devolver al controlador.
                .collectList();
    }
}
