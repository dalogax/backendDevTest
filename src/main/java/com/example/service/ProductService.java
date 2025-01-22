package com.example.service;

import com.example.model.ProductDetail;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductService {

    @Autowired
    private RestTemplate restTemplate;

    public List<ProductDetail> getSimilarProducts(String productId) {
        List<String> similarProductIds = getSimilarProductIds(productId);
        List<ProductDetail> similarProducts = new ArrayList<>();

        for (String id : similarProductIds) {
            ProductDetail productDetail = getProductDetail(id);
            if (productDetail != null) {
                similarProducts.add(productDetail);
            }
        }

        return similarProducts;
    }

    private List<String> getSimilarProductIds(String productId) {
        String url = "http://localhost:3001/product/" + productId + "/similarids";
        return restTemplate.getForObject(url, List.class);
    }

    private ProductDetail getProductDetail(String productId) {
        String url = "http://localhost:3001/product/" + productId;
        try {
            return restTemplate.getForObject(url, ProductDetail.class);
        } catch (Exception e) {
            return null;
        }
    }
}
