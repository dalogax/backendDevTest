package com.example.demo.core.use_case;

import com.example.demo.core.domain.Product;
import com.example.demo.core.domain.ProductRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;

@Service
public class GetSimilarProducts {

    private final ProductRepository productRepository;

    public GetSimilarProducts(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Response execute(Request request) {
        Set<String> similarProductIDs = productRepository.getSimilarProductIDs(request.getProductId());
        Set<Product> similarProducts = getSimilarProducts(similarProductIDs);

        return new Response(similarProducts);
    }

    private Set<Product> getSimilarProducts(Set<String> similarProductIDs) {
        return similarProductIDs.stream()
                .map(productRepository::getProduct)
                .collect(Collectors.toSet());
    }

    @Getter
    @AllArgsConstructor
    public static class Request {
        private String productId;
    }

    @Getter
    @AllArgsConstructor
    public static class Response {
        private Set<Product> similarProducts;
    }
}
