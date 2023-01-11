package com.inditex.myapp.infrastructure.service.impl;

import com.inditex.myapp.domain.model.ProductDetail;
import com.inditex.myapp.domain.service.ExistingApiService;

import com.inditex.myapp.infrastructure.rest.DefaultApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExistingApiServiceImpl implements ExistingApiService {

    @Autowired
    private DefaultApi defaultApi;

    @Override
    public ProductDetail getProduct(String productId) {



        defaultApi.getProductProductId(productId);
        return null;
    }

    @Override
    public List<String> getSimilarProducts(String productId) {
        defaultApi.getProductSimilarids(productId);
        return null;
    }
}
