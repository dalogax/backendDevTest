package com.inditex.myapp.domain.service;

import com.inditex.myapp.domain.model.ProductDetail;

import java.util.List;

public interface ExistingApiService {

    ProductDetail getProduct(String productId);

    List<String> getSimilarProducts(String productId);

}
