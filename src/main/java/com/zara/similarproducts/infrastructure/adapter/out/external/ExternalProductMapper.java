package com.zara.similarproducts.infrastructure.adapter.out.external;

import com.zara.similarproducts.domain.model.Product;
import com.zara.similarproducts.domain.model.ProductId;
import com.zara.similarproducts.infrastructure.adapter.out.external.dto.ExternalProductResponse;
import org.springframework.stereotype.Component;

@Component
public class ExternalProductMapper {
    
    public Product toDomain(ExternalProductResponse response) {
        return Product.of(
                ProductId.of(response.id()),
                response.name(),
                response.price(),
                response.availability()
        );
    }
}