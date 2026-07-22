package com.knowmad.similarproducts.model;

/**
 * Representa el detalle completo de un producto del catálogo.
 *
 * <p>Modelado como {@code record} de Java para ser inmutable y conciso.
 * Los campos se corresponden directamente con el esquema {@code ProductDetail}
 * definido en {@code similarProducts.yaml} y {@code existingApis.yaml}.</p>
 *
 * <p>Jackson (incluido en Spring WebFlux) deserializa automáticamente el JSON
 * del servicio externo en esta clase usando los nombres de campo como claves.</p>
 *
 * @param id           identificador único del producto (p.ej. {@code "1"})
 * @param name         nombre del producto (p.ej. {@code "Shirt"})
 * @param price        precio del producto (p.ej. {@code 9.99})
 * @param availability {@code true} si el producto está disponible, {@code false} si no
 */
public record ProductDetail(
        String id,
        String name,
        Double price,
        Boolean availability
) {}
