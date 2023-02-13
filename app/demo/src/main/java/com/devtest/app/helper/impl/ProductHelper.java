package com.devtest.app.helper.impl;

import com.devtest.app.entities.Product;
import com.devtest.app.helper.IProductHelper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

public class ProductHelper implements IProductHelper{
    private final String URL = "http://localhost:3001/product/";
    private final String PATH = "/similarids";


    public String[] getSimilarProductsIds(String productId) {
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<String[]> response =
                    restTemplate.getForEntity(URL + productId + PATH,
                            String[].class);
            return response.getBody();
        } catch (Exception e){
            return null;
        }
    }

    public Product getProductById(String productId) {
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<Product> response = restTemplate.getForEntity(URL + productId, Product.class);
            return response.getBody();
        } catch (Exception e){
            return null;
        }
    }
}
