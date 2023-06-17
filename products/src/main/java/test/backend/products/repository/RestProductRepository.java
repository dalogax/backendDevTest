package test.backend.products.repository;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.client.RestTemplate;
import test.backend.products.entity.ProductDetail;

import java.util.*;

@Slf4j
public class RestProductRepository implements ProductRepository {

    private final RestTemplate restTemplate;
    private final String productUrl;


    public RestProductRepository(RestTemplate restTemplate, String productUrl) {
        this.restTemplate = restTemplate;
        this.productUrl = productUrl;
    }

    @Override
    public Optional<List<String>> getSimilarProductsById(String productId) {
        log.info("Retrieving ids of similar products to product with id: " + productId);
        Map<String, String> params = new HashMap<>();
        params.put("productId", productId);
        String[] response = restTemplate.getForObject(productUrl + "/similarids", String[].class, params);
        if (response == null) {
            return Optional.empty();
        }
        return Optional.of(Arrays.asList(response));
    }

    @Override
    public Optional<ProductDetail> getProductDetailById(String productId) {
        log.info("Retrieving details of product with id: " + productId);
        Map<String, String> params = new HashMap<>();
        params.put("productId", productId);
        ProductDetail response = restTemplate.getForObject(productUrl, ProductDetail.class, params);
        return Optional.ofNullable(response);
    }
}
