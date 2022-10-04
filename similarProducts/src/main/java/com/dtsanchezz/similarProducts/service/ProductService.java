package com.dtsanchezz.similarProducts.service;

import com.dtsanchezz.similarProducts.model.ProductDetail;

import java.util.List;

public interface ProductService {

    List<ProductDetail> getSimilarProducts(String productId);
}
