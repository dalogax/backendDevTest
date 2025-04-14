package com.nunegal.backendDevTest.service;

import com.nunegal.backendDevTest.client.ProductClient;
import com.nunegal.backendDevTest.model.Product;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Servicio encargado de obtener productos similares a uno dado.
 * Usa el cliente REST para interactuar con el mock de productos.
 */
@Service
public class SimilarProductService {

    private final ProductClient productClient;

    public SimilarProductService(ProductClient productClient) {
        this.productClient = productClient;
    }

    public List<Product> getSimilarProducts(String productId) {
        List<Integer> similarIds = productClient.getSimilarProductIds(productId);
        List<Product> similarProducts = new ArrayList<>();

        for (Integer id : similarIds) {
            Product product = productClient.getProductById(id.toString());
            similarProducts.add(product);
        }

        return similarProducts;
    }
}
