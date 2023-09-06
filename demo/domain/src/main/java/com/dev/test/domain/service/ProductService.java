package com.dev.test.domain.service;

import java.util.List;

import com.dev.test.domain.aggregate.ProductDetail;

public interface ProductService {
	ProductDetail provideDetailById(String productId);
	List<Integer> provideSimilarIdListById(String productId);

}
