package com.knowmad.similarproducts;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Punto de entrada de la aplicación Similar Products API.
 *
 * <p>Aplicación Spring Boot reactiva (WebFlux) que expone el endpoint
 * {@code GET /product/{productId}/similar} en el puerto 5000.</p>
 *
 * <p>La aplicación actúa como agregador: recibe una petición de productos
 * similares, consulta el servicio externo para obtener los IDs similares
 * y luego recupera el detalle de cada uno en paralelo, devolviendo la
 * lista completa al cliente de forma eficiente y resiliente.</p>
 *
 * <p>Configuración principal en {@code src/main/resources/application.yml}.</p>
 */
@SpringBootApplication
public class SimilarProductsApplication {

    /**
     * Método principal que inicia el contexto de Spring Boot.
     *
     * @param args argumentos de línea de comandos (no utilizados)
     */
    public static void main(String[] args) {
        SpringApplication.run(SimilarProductsApplication.class, args);
    }
}
