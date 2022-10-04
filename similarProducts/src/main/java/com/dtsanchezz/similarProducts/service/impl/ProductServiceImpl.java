package com.dtsanchezz.similarProducts.service.impl;

import com.dtsanchezz.similarProducts.consumer.api.ProductConsumer;
import com.dtsanchezz.similarProducts.model.ProductDetail;
import com.dtsanchezz.similarProducts.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductServiceImpl implements ProductService {

    private final ProductConsumer productConsumer;

    @Autowired
    public ProductServiceImpl(final ProductConsumer productConsumer) {
        this.productConsumer = productConsumer;
    }


    @Override
    public List<ProductDetail> getSimilarProducts(final String productId) {
        final List<String> similarIds = this.productConsumer.getSimilarIds(productId);

        return similarIds.stream()
                .map(this.productConsumer::getProductDetail)
                .collect(Collectors.toList());
    }
}
