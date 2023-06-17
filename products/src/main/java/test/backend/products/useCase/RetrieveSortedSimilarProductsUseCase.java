package test.backend.products.useCase;

import test.backend.products.entity.ProductDetail;
import test.backend.products.repository.ProductRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class RetrieveSortedSimilarProductsUseCase {

    private final ProductRepository productRepository;

    public RetrieveSortedSimilarProductsUseCase(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public List<ProductDetail> execute(String productId) {
        List<String> similarProducts = productRepository.getSimilarProductsById(productId).orElse(new ArrayList<>());
        return similarProducts.stream()
                .map(id -> productRepository.getProductDetailById(id).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

}
