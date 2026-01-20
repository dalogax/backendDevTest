package com.similarproducts.service;

import com.similarproducts.model.ProductDetail;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class SimilarProductsService {

    private static final Logger logger = LoggerFactory.getLogger(SimilarProductsService.class);

    private final ExternalApiService externalApiService;
    private final ExecutorService executorService;

    private static final int THREAD_POOL_SIZE = 10;
    private static final int PRODUCT_TIMEOUT_SECONDS = 15;

    public SimilarProductsService(ExternalApiService externalApiService) {
        this.externalApiService = externalApiService;
        this.executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    }

    public List<ProductDetail> getSimilarProducts(String productId) {
        try {
            String[] similarIds = externalApiService.getSimilarProductIds(productId);

            if (similarIds == null || similarIds.length == 0) {
                return new ArrayList<>();
            }

            List<CompletableFuture<ProductDetail>> futures = new ArrayList<>();

            for (String id : similarIds) {
                CompletableFuture<ProductDetail> future = CompletableFuture.supplyAsync(() -> {
                    return externalApiService.getProductDetail(id);
                }, executorService);
                futures.add(future);
            }

            List<ProductDetail> similarProducts = futures.stream()
                    .map(f -> {
                        try {
                            return f.get(PRODUCT_TIMEOUT_SECONDS, TimeUnit.SECONDS);
                        } catch (TimeoutException | InterruptedException | ExecutionException e) {
                            logger.warn("Failed to fetch product details within timeout ({}s). Error: {}",
                                    PRODUCT_TIMEOUT_SECONDS, e.getMessage());
                            return null;
                        }
                    })
                    .filter(Objects::nonNull)
                    .collect(Collectors.toList());

            return similarProducts;

        } catch (Exception e) {
            logger.error("Unexpected error in getSimilarProducts for productId: {}. Error: {}", productId,
                    e.getMessage(), e);
            return new ArrayList<>();
        }
    }
}
