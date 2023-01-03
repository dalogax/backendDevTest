package com.prueba.products.service.impl;

import com.prueba.products.model.Product;
import com.prueba.products.productsClient.ProductsClient;
import com.prueba.products.service.SimilarProductsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarProductsServiceImpl  implements SimilarProductsService{

    @Autowired
    private final ProductsClient productsClient;

    public List<Product> getSimilarProducts(String productId) {

        if( productId == null) return null;

        String[] similarProductsIds =  productsClient.getSimilarProductsIds(productId);
        if( similarProductsIds == null) return null;

        List<Product> products = new ArrayList<>();
        for (String id : similarProductsIds) {
           Product product = productsClient.getProductById(id);
           if( product != null) {
               products.add(product);
           }
        }

        return products;
    }
}
