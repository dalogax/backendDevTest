package org.challenge.products.infrastructure.client;

import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.model.Product;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductClient implements ProductPort {

    //TODO: implement the client to call the external service and get the similar product ids, for now we are returning a hardcoded list of similar product ids
    @Override
    public List<String> getSimilarProductIds(String productId) {
        return List.of("1", "2", "3", "4", "5");
    }

    //TODO: Implement the client to call the external service to get the product details. For now, we are returning a dummy product detail.
    @Override
    public Product getProductDetail(String productId) {
       return new Product(productId, null, null, null);
    }

}