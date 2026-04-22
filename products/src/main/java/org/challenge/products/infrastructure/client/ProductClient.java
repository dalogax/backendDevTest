package org.challenge.products.infrastructure.client;

import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.model.Product;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.List;

@Component
public class ProductClient implements ProductPort {

    private final RestClient productRestClient;

    public ProductClient(RestClient productRestClient) {
        this.productRestClient = productRestClient;
    }

    @Override
    public List<String> getSimilarProductIds(String productId) {
        List<Integer> ids = productRestClient.get()
                .uri("/{productId}/similarids", productId)
                .retrieve()
                .body(ParameterizedTypeReference.forType(List.class));
        return ids.stream().map(Object::toString).toList();
    }

    //TODO: Implement the client to call the external service to get the product details. For now, we are returning a dummy product detail.
    @Override
    public Product getProductDetail(String productId) {
       return new Product(productId, null, null, null);
    }

}