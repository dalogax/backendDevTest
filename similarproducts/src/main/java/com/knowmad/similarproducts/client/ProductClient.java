package com.knowmad.similarproducts.client;

import com.knowmad.similarproducts.model.ProductDetail;
import com.knowmad.similarproducts.model.ProductNotFoundException;
import io.netty.channel.ChannelOption;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import reactor.netty.http.client.HttpClient;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Cliente HTTP reactivo para comunicarse con el servicio externo de productos.
 *
 * <p>Encapsula todas las llamadas al API externo ({@code localhost:3001} por defecto)
 * usando {@link WebClient} de Spring WebFlux sobre Netty. Gestiona dos tipos de
 * llamadas: obtención de IDs similares y obtención de detalle de producto.</p>
 *
 * <p>Los timeouts se configuran a nivel de conexión TCP y de respuesta HTTP
 * directamente en el cliente Netty subyacente, garantizando que ninguna llamada
 * quede bloqueada indefinidamente.</p>
 *
 * <p>Configuración relevante en {@code application.yml}:</p>
 * <pre>
 *   product.service.base-url    → URL base del servicio externo
 *   product.service.timeout-ms  → timeout de respuesta en milisegundos (defecto: 2000)
 * </pre>
 */
@Component
public class ProductClient {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final WebClient webClient;

    /**
     * Construye el cliente HTTP configurando los timeouts de red.
     *
     * <p>Se configuran dos niveles de timeout:</p>
     * <ul>
     *   <li><b>Timeout de conexión TCP</b> (fijo a 1 s): tiempo máximo para
     *       establecer la conexión con el servidor remoto.</li>
     *   <li><b>Timeout de respuesta HTTP</b> (configurable): tiempo máximo desde
     *       que se envía la petición hasta que se reciben las cabeceras de respuesta.
     *       Por defecto 2 000 ms, ajustable con {@code product.service.timeout-ms}.</li>
     * </ul>
     *
     * @param baseUrl   URL base del servicio externo (p.ej. {@code http://localhost:3001})
     * @param timeoutMs tiempo máximo de espera de respuesta en milisegundos
     */
    public ProductClient(
            @Value("${product.service.base-url}") String baseUrl,
            @Value("${product.service.timeout-ms:2000}") long timeoutMs) {

        // Configuramos el cliente Netty con timeouts explícitos para evitar que
        // peticiones a productos lentos (como product/1000 con 5 s de delay)
        // bloqueen el pool de conexiones bajo alta concurrencia.
        HttpClient httpClient = HttpClient.create()
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 1000)
                .responseTimeout(Duration.ofMillis(timeoutMs));

        this.webClient = WebClient.builder()
                .baseUrl(baseUrl)
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }

    /**
     * Obtiene la lista de identificadores de productos similares para un producto dado.
     *
     * <p>Realiza una llamada GET a {@code /product/{id}/similarids} en el servicio externo.
     * Los IDs devueltos pueden ser números enteros en el JSON (p.ej. {@code [2, 3, 4]}),
     * por lo que se deserializan como {@code List<Object>} y se convierten a {@code String}
     * para uniformidad con el resto del dominio.</p>
     *
     * <p>Comportamiento ante errores:</p>
     * <ul>
     *   <li><b>404</b>: lanza {@link ProductNotFoundException} para que el controlador
     *       devuelva un HTTP 404 al cliente.</li>
     *   <li><b>Timeout / red</b>: el error se propaga hacia arriba sin consumir;
     *       el controlador lo trata como error de servidor (5xx).</li>
     * </ul>
     *
     * @param productId identificador del producto del que se quieren obtener similares
     * @return {@code Mono} que emite la lista ordenada de IDs similares,
     *         o completa con error si el producto no existe
     */
    public Mono<List<String>> getSimilarIds(String productId) {
        return webClient.get()
                .uri("/product/{id}/similarids", productId)
                .retrieve()
                // Si el servicio externo responde con 404, convertimos el error a nuestra
                // excepción de dominio para que el controlador pueda distinguirlo.
                .onStatus(
                        status -> status.value() == 404,
                        response -> {
                            log.warn("Producto no encontrado al consultar similarids: productId={}", productId);
                            return Mono.error(new ProductNotFoundException(productId));
                        })
                // Deserializamos como List<Object> porque el servicio externo devuelve
                // números (e.g. [2,3,4]) en lugar de strings. Luego los convertimos a String.
                .bodyToMono(new ParameterizedTypeReference<List<Object>>() {})
                .map(list -> list.stream()
                        .map(Object::toString)
                        .collect(Collectors.toList()));
    }

    /**
     * Obtiene el detalle completo de un producto a partir de su identificador.
     *
     * <p>Realiza una llamada GET a {@code /product/{id}} en el servicio externo.
     * Este método está diseñado para ser invocado en paralelo para múltiples IDs,
     * por lo que ante cualquier problema simplemente omite el producto afectado
     * en lugar de fallar toda la operación.</p>
     *
     * <p>Comportamiento ante errores (todos resultan en omitir el producto):</p>
     * <ul>
     *   <li><b>404</b>: el producto no existe en el catálogo, se descarta.</li>
     *   <li><b>5xx</b>: error interno del servicio externo, se descarta.</li>
     *   <li><b>Timeout</b>: el producto tarda más del límite configurado
     *       ({@code product.service.timeout-ms}), se descarta.</li>
     *   <li><b>Error de red</b>: fallo de conectividad, se descarta.</li>
     * </ul>
     *
     * @param productId identificador del producto a consultar
     * @return {@code Mono} que emite el {@link ProductDetail} si la llamada tiene éxito,
     *         o {@code Mono.empty()} si ocurre cualquier error (el producto se omite)
     */
    public Mono<ProductDetail> getProductDetail(String productId) {
        return webClient.get()
                .uri("/product/{id}", productId)
                .retrieve()
                // Convertimos cualquier respuesta de error (4xx o 5xx) en una excepción
                // para poder capturarla de forma uniforme en el onErrorResume siguiente.
                .onStatus(
                        status -> status.isError(),
                        response -> Mono.error(new RuntimeException("Error fetching product: " + productId)))
                .bodyToMono(ProductDetail.class)
                // Estrategia de resiliencia: ante cualquier error (incluido timeout de Netty,
                // error de red o excepción de deserialización) devolvemos Mono.empty().
                // Esto hace que flatMapSequential en el servicio simplemente ignore este
                // producto sin interrumpir la respuesta al cliente.
                .onErrorResume(e -> {
                    log.warn("Producto descartado (error o timeout): productId={}, causa={}", productId, e.getMessage());
                    return Mono.empty();
                });
    }
}
