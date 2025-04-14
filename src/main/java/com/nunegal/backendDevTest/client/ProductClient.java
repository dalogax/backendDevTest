package com.nunegal.backendDevTest.client;

import com.nunegal.backendDevTest.model.Product;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Component
public class ProductClient {

    @Value("${external.api.url}")
    private String apiBaseUrl;

    private final RestTemplate restTemplate = new RestTemplate();

    public List<Integer> getSimilarProductIds(String productId) {
        String url = apiBaseUrl + "/product/" + productId + "/similarids";

        try {
            Integer[] ids = restTemplate.getForObject(url, Integer[].class);
            return Arrays.asList(ids);
        } catch (RestClientException e) {
            throw new RuntimeException("Error retrieving similar product IDs: " + e.getMessage(), e);
        }
    }

    public Product getProductById(String productId) {
        String url = apiBaseUrl + "/product/" + productId;

        try {
            return restTemplate.getForObject(url, Product.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Error retrieving product with ID " + productId + ": " + e.getMessage(), e);
        }
    }
}