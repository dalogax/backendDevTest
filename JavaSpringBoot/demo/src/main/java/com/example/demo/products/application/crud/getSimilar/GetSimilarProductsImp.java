package com.example.demo.products.application.crud.getSimilar;

import com.example.demo.products.application.dto.ProductApplication;
import com.example.demo.products.infrastructure.http_outputAdapter.ProductApiClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class GetSimilarProductsImp implements GetSimilarProducts {

    private final ProductApiClient productApiClient;

    @Override
    public List<ProductApplication> getSimilarProductsById(String productId) {
        log.info("Fetching similar products for productId: {}", productId);

        try {
            //1 Obtener IDs de productos similares (bloqueando para simplificar)
            //Spring WebFlux -> reactivo -> usa Mono -> necesario blokear
            List<String> similarIds = productApiClient.getSimilarProductIds(productId)
                    .block();

            if (similarIds == null || similarIds.isEmpty()) {
                log.info("No similar products found for productId: {}", productId);
                return List.of();
            }

            log.debug("Found {} similar product IDs for productId: {}", similarIds.size(), productId);

            // 2. Obtener detalles de cada producto en paralelo
            Flux<ProductApplication> productsFlux = productApiClient.getProductDetails(similarIds);

            // 3. Recopilar todos los productos (bloqueando para simplificar)
            List<ProductApplication> products = productsFlux
                    .collectList()
                    .block();

            if (products == null) {
                log.warn("Failed to fetch product details for productId: {}", productId);
                return List.of();
            }

            // 4. Filtrar productos nulos (por si algún producto falló)
            List<ProductApplication> validProducts = products.stream()
                    .filter(product -> product != null)
                    .toList();

            log.info("Successfully retrieved {} similar products for productId: {}", 
                    validProducts.size(), productId);

            // Los productos ya vienen ordenados por similitud según el mock server
            return validProducts;

        } catch (Exception e) {
            log.error("Error retrieving similar products for productId: {}", productId, e);
            throw new RuntimeException("Error retrieving similar products: " + e.getMessage(), e);
        }
    }
}

