package com.inditex.myapp.domain.service;

import com.inditex.myapp.domain.model.ProductDetail;

import java.util.List;

public interface ProductService {

    List<ProductDetail> productSimilar(String productId);

}
