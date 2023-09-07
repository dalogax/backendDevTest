package com.product.service.impl;

import com.product.client.ProductClient;
import com.product.dto.ProductDetailDTO;
import com.product.dto.SimilarProductDTO;
import com.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;


import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {
    
    private final ProductClient productClient;
    @Override
    public SimilarProductDTO findSimilarProducts(String productId) {
        if (productId == null) {
            log.warn("productId is null. Cannot search for similar products.");
            return null;
        }

        List<Integer> similarProductsIds = productClient.findSimilarProductsIds(productId);

        if (CollectionUtils.isEmpty(similarProductsIds)) {
            log.info("No similar products found for productId: {}", productId);
            return null;
        }

        SimilarProductDTO products = new SimilarProductDTO();
        List<ProductDetailDTO> productDetails = similarProductsIds.stream()
                .map(productClient::findProductById)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
        if (CollectionUtils.isEmpty(productDetails)) {
            log.info("No product details found for similar products.");
            return null;
        }

        products.setDetails(productDetails);

        log.info("Found {} similar products for productId: {}", products.getDetails().size(), productId);

        return products;
    }
}
