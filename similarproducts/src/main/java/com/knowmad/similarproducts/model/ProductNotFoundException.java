package com.knowmad.similarproducts.model;

/**
 * Excepción de dominio que indica que un producto no existe en el catálogo.
 *
 * <p>Se lanza cuando el servicio externo responde con HTTP 404 al consultar
 * los IDs similares de un producto. El controlador la captura para devolver
 * una respuesta HTTP 404 al cliente, conforme al contrato de la API.</p>
 *
 * <p>Al extender {@link RuntimeException} no es necesario declararla en las
 * firmas de los métodos reactivos, lo que simplifica la cadena de operadores.</p>
 */
public class ProductNotFoundException extends RuntimeException {

    /**
     * Crea la excepción con un mensaje descriptivo que incluye el ID del producto.
     *
     * @param productId identificador del producto que no se ha encontrado
     */
    public ProductNotFoundException(String productId) {
        super("Product not found: " + productId);
    }
}
