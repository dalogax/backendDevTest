package com.globant.interview.david.msdavidmobilephone.application.usecase;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import com.globant.interview.david.msdavidmobilephone.domain.model.SimilarProducts;
import com.globant.interview.david.msdavidmobilephone.domain.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class GetSimilarProductsUseCase {

    private final ProductRepository productRepository;

    @Cacheable(value = "similarProducts", key = "#productId")
    public SimilarProducts execute(String productId) {
        log.info("Getting similar products for productId: {}", productId);

        List<String> similarIds = productRepository.getSimilarProductIds(productId);

        if (similarIds.isEmpty()) {
            log.warn("No similar products found for productId: {}", productId);
            return SimilarProducts.empty();
        }

        List<Product> products = similarIds.parallelStream()
                .map(productRepository::getProductDetail)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());

        log.info("Found {} similar products for productId: {}", products.size(), productId);
        return new SimilarProducts(products);
    }
}
