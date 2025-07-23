package com.nicolas_sancho.similar_products.service;

import com.nicolas_sancho.similar_products.client.ProductApiClient;
import com.nicolas_sancho.similar_products.dto.ProductDetailDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductApiClient productApiClient;
    private final ExecutorService executorService;

    @Override
    public List<ProductDetailDTO> getSimilarProducts(String productId) {
        List<String> similarIds = productApiClient.getSimilarIds(productId);

        List<CompletableFuture<ProductDetailDTO>> futures = similarIds.stream()
                .map(id -> CompletableFuture.supplyAsync(
                        () -> productApiClient.getProductDetail(id),
                        executorService))
                .toList();

        return futures.stream()
                .map(CompletableFuture::join)
                .collect(Collectors.toList());
    }
}
