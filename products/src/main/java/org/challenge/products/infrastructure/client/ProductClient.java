package org.challenge.products.infrastructure.client;

import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.model.Product;
import org.challenge.products.infrastructure.dto.ProductDetailDto;
import org.challenge.products.mapper.ProductMapper;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductClient implements ProductPort {

    private final RestClient productRestClient;
    private final ProductMapper productMapper;

    public ProductClient(RestClient productRestClient, ProductMapper productMapper) {
        this.productRestClient = productRestClient;
        this.productMapper = productMapper;
    }

    @Override
    public List<String> getSimilarProductIds(String productId) {
        List<Integer> ids = productRestClient.get()
                .uri("/{productId}/similarids", productId)
                .retrieve()
                .body(ParameterizedTypeReference.forType(List.class));
        return ids.stream().map(Object::toString).toList();
    }

    @Override
    public Product getProductDetail(String productId) {
        ProductDetailDto responseBody = productRestClient.get()
                .uri("/{productId}", productId)
                .retrieve()
                .body(ProductDetailDto.class);
        return productMapper.toModel(responseBody);
    }

}