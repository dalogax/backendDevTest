package com.inditex.myapp.domain.service;

public interface ExistingApiService {

    void getProduct(String productId);

    void getSimilarProducts(String productId);

}
