package org.mybranch.shop.products.application.search_similar_products;

import lombok.RequiredArgsConstructor;
import org.mybranch.shop.products.domain.ProductNotFoundError;
import org.mybranch.shop.products.domain.ProductDetail;
import org.mybranch.shop.products.infrastructure.clients.ProductsRestClient;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public final class FindSimilarProductsById {

    private final ProductsRestClient productsRestClient;

    public List<ProductDetail> findSimilarProductsById(String productId) {

        List<String> similarIds = productsRestClient.similarIds(productId);

        if (CollectionUtils.isEmpty(similarIds)) {
            throw new ProductNotFoundError("Similar ids not found");
        }

        return similarIds.stream()
                .map(productsRestClient::productDetail)
                .collect(Collectors.toList());
    }

}