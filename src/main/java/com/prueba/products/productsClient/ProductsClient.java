package com.prueba.products.productsClient;

import com.prueba.products.model.Product;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
public class ProductsClient {

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
            log.warn("getSimilarProductsIds:: Not found similar products ids with productId: {}", productId);
            return null;
        }
    }

    public Product getProductById(String productId) {
        RestTemplate restTemplate = new RestTemplate();
        try{
            ResponseEntity<Product> response = restTemplate.getForEntity(URL + productId, Product.class);
            return response.getBody();
        } catch (Exception e){
            log.warn("getProductById:: Not found product detail with productId: {}", productId);
            return null;
        }
    }

}
