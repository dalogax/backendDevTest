package com.globant.interview.david.msdavidmobilephone.domain.repository;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ReactiveProductRepository {

    Flux<String> getSimilarProductIds(String productId);

    Mono<Product> getProductDetail(String productId);
}
