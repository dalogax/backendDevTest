package com.inditex.myapp.infrastructure.service.impl;

import com.inditex.myapp.domain.service.ExistingApiService;

import com.inditex.myapp.infrastructure.rest.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExistingApiServiceImpl implements ExistingApiService {

    @Autowired
    private DefaultApi defaultApi;

    @Override
    public void getProduct(String productId) {



        defaultApi.getProductProductId(productId);
    }

    @Override
    public void getSimilarProducts(String productId) {
        defaultApi.getProductSimilarids(productId);
    }
}
