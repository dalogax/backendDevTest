package com.nunegal.backendDevTest.controller;

import com.nunegal.backendDevTest.model.Product;
import com.nunegal.backendDevTest.service.SimilarProductService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controlador que expone el endpoint REST para obtener productos similares.
 */
@RestController
@RequestMapping("/product")
public class SimilarProductController {

    private final SimilarProductService similarProductService;

    public SimilarProductController(SimilarProductService similarProductService) {
        this.similarProductService = similarProductService;
    }

    /**
     * Devuelve la lista de productos similares al producto dado.
     *
     * @param productId ID del producto principal
     * @return Lista de productos similares
     */
    @GetMapping("/{productId}/similar")
    // De esta forma se puede verificar que el endpoint devuelve el código de estado
    // correcto y el cuerpo de la respuesta correcto usando postman.
    public ResponseEntity<List<Product>> getSimilarProducts(@PathVariable String productId) {
        List<Product> similarProducts = similarProductService.getSimilarProducts(productId);
        return ResponseEntity.ok(similarProducts);
    }
}
