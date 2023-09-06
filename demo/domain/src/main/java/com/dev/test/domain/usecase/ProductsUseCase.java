package com.dev.test.domain.usecase;

import com.dev.test.domain.aggregate.SimilarProducts;

public interface ProductsUseCase {
	SimilarProducts provideSimilarProducts(String productId);
}
