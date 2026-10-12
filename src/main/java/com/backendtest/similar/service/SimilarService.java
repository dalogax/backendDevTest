package com.backendtest.similar.service;

import com.backendtest.similar.application.port.in.SimilarProductsUseCase;
import com.backendtest.similar.config.ProductProperties;
import com.backendtest.similar.dto.ProductDetail;
import com.backendtest.similar.port.ProductCatalogPort;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SimilarService implements SimilarProductsUseCase {

    private static final Logger log = LoggerFactory.getLogger(SimilarService.class);

    private final ProductCatalogPort catalog;
    private final long timeoutMs;
    private final Executor executor;

    @Autowired
    public SimilarService(ProductCatalogPort catalog, ProductProperties props, Executor similarProductsExecutor) {
        this(catalog, props.timeoutMs(), similarProductsExecutor);
    }

    SimilarService(ProductCatalogPort catalog, long timeoutMs, Executor executor) {
        this.catalog = catalog;
        this.timeoutMs = timeoutMs;
        this.executor = executor;
    }

    @Override
    public List<ProductDetail> getSimilarProducts(String productId) {
        validate(productId);
        long start = System.currentTimeMillis();
        List<String> ordered = dedupe(catalog.getSimilarIds(productId));
        if (ordered.isEmpty()) {
            return List.of();
        }
        List<ProductDetail> result = collect(fetchInParallel(ordered));
        log.info(
                "similar productId={} total={} returned={} filtered={} elapsedMs={}",
                productId,
                ordered.size(),
                result.size(),
                ordered.size() - result.size(),
                System.currentTimeMillis() - start);
        return result;
    }

    private void validate(String productId) {
        if (productId == null || productId.isBlank()) {
            throw new IllegalArgumentException("productId must not be blank");
        }
    }

    private List<String> dedupe(List<String> ids) {
        return new ArrayList<>(new LinkedHashSet<>(ids));
    }

    private List<CompletableFuture<ProductDetail>> fetchInParallel(List<String> ordered) {
        return ordered.stream()
                .map(id -> CompletableFuture.supplyAsync(
                                () -> catalog.getProduct(id).orElse(null), executor)
                        .orTimeout(timeoutMs, TimeUnit.MILLISECONDS)
                        .exceptionally(ex -> null))
                .toList();
    }

    private List<ProductDetail> collect(List<CompletableFuture<ProductDetail>> futures) {
        List<ProductDetail> result = new ArrayList<>();
        for (CompletableFuture<ProductDetail> f : futures) {
            try {
                ProductDetail d = f.join();
                if (d != null) {
                    result.add(d);
                }
            } catch (Exception e) {
                // filtered
            }
        }
        return result;
    }
}
