package com.globant.interview.david.msdavidmobilephone.domain.repository;

import com.globant.interview.david.msdavidmobilephone.domain.model.Product;

import java.util.List;
import java.util.Optional;

public interface ProductRepository {

    List<String> getSimilarProductIds(String productId);

    Optional<Product> getProductDetail(String productId);
}
