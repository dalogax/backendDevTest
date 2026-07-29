package com.example.TechnicalAssessment.service;

import com.example.TechnicalAssessment.client.ProductApiClient;
import com.example.TechnicalAssessment.model.ProductDetail;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.stereotype.Service;

@Service
public class SimilarProductService {

    private final ProductApiClient productApiClient;
    private final Executor virtualThreadTaskExecutor;

    public SimilarProductService(
            ProductApiClient productApiClient,
            Executor virtualThreadTaskExecutor
    ) {
        this.productApiClient = productApiClient;
        this.virtualThreadTaskExecutor = virtualThreadTaskExecutor;
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        List<String> similarProductIds = productApiClient.getSimilarProductIds(productId);

        if (similarProductIds.isEmpty()) {
            return List.of();
        }

        List<CompletableFuture<ProductDetail>> detailFutures = similarProductIds.stream()
                .distinct()
                .map(similarProductId -> CompletableFuture.supplyAsync(
                        () -> productApiClient.getProductDetail(similarProductId),
                        virtualThreadTaskExecutor
                ).exceptionally(exception -> null))
                .toList();

        return detailFutures.stream()
                .map(CompletableFuture::join)
                .filter(productDetail -> productDetail != null)
                .toList();
    }
}
