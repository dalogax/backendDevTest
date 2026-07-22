package com.knowmad.similarproducts.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

/**
 * Manejador global de excepciones no controladas para la capa REST.
 *
 * <p>Captura cualquier excepción que escape de los controladores sin ser gestionada
 * explícitamente (p.ej. errores de red en {@code getSimilarIds} que no son 404)
 * y devuelve una respuesta JSON con código HTTP 500 en lugar del HTML de error
 * por defecto de Spring.</p>
 *
 * <p>Las excepciones de dominio conocidas (como {@link com.knowmad.similarproducts.model.ProductNotFoundException})
 * son manejadas directamente en el controlador mediante {@code onErrorResume} y
 * nunca llegan a este handler.</p>
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    /**
     * Maneja cualquier excepción inesperada que no haya sido capturada previamente.
     *
     * @param ex la excepción no controlada
     * @return mapa con el mensaje de error en formato JSON
     */
    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public Map<String, String> handleUnexpectedException(Exception ex) {
        log.error("Error inesperado no controlado: {}", ex.getMessage(), ex);
        return Map.of("error", "Se ha producido un error interno. Por favor, inténtelo de nuevo más tarde.");
    }
}
