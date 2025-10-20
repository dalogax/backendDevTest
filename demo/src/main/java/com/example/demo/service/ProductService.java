package com.example.demo.service;

import com.example.demo.client.ExternalApiClient;
import com.example.demo.model.Product;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.util.List;

@Service
public class ProductService {
    
    private final ExternalApiClient apiClient;
    
    public ProductService(ExternalApiClient apiClient) {
        this.apiClient = apiClient;
    }
    
    public List<Product> getSimilarProducts(String productId) {
        // Primero: obtener los IDs similares
        List<String> similarIds = apiClient.getSimilarIds(productId)
                .block(); // Bloquea y espera el resultado
        
        if (similarIds == null || similarIds.isEmpty()) {
            System.err.println("No se encontraron productos similares para ID: " + productId);
            return List.of();
        }
        
        System.out.println("Productos similares encontrados: " + similarIds);
        
        // Segundo: obtener detalles en PARALELO
        return Flux.fromIterable(similarIds)
                .parallel()  // Activa paralelización
                .runOn(Schedulers.parallel())  // Usa thread pool
                .flatMap(id -> {
                    System.out.println("Obteniendo producto: " + id);
                    return apiClient.getProductDetails(String.valueOf(id));
                })
                .sequential()  // Vuelve a secuencial para colectar
                .collectList()
                .block();  // Espera todos los resultados
    }
}