package com.pablolizarraga.solution.application.service;

import com.pablolizarraga.solution.application.exception.NotFoundException;
import com.pablolizarraga.solution.application.port.in.SimilarProductServicePort;
import com.pablolizarraga.solution.application.port.out.SimilarProductRepositoryPort;
import com.pablolizarraga.solution.domain.ProductDetail;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@RequiredArgsConstructor
@Service
public class SimilarProductService implements SimilarProductServicePort {

    private final SimilarProductRepositoryPort similarProductRepository;

    @Override
    public List<ProductDetail> getSimilarProducts(String productId) {
        validateIfProductExists(productId);

        return similarProductRepository
                .getSimilarProductIds(productId)
                .stream()
                .map(similarProductRepository::getProduct)
                .flatMap(Optional::stream)
                .toList();
    }

    private void validateIfProductExists(String productId) {
        similarProductRepository.getProduct(productId)
                .ifPresentOrElse(
                        p -> {},
                        () -> {throw new NotFoundException("Product not found with ID: " + productId);});
    }
}
