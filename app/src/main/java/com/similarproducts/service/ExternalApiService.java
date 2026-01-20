package com.similarproducts.service;

import com.similarproducts.model.ProductDetail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.concurrent.TimeUnit;

@Service
public class ExternalApiService {

    private final RestTemplate restTemplate;
    private final String mockBaseUrl;

    public ExternalApiService(
            @Value("${external.api.mock.url:http://localhost:3001}") String mockBaseUrl) {
        this.mockBaseUrl = mockBaseUrl;
        this.restTemplate = createRestTemplate();
    }

    public String[] getSimilarProductIds(String productId) {
        String url = mockBaseUrl + "/product/" + productId + "/similarids";
        try {
            String[] ids = restTemplate.getForObject(url, String[].class);
            return ids != null ? ids : new String[0];
        } catch (Exception e) {
            return new String[0];
        }
    }

    public ProductDetail getProductDetail(String productId) {
        String url = mockBaseUrl + "/product/" + productId;
        try {
            ProductDetail detail = restTemplate.getForObject(url, ProductDetail.class);
            return detail;
        } catch (Exception e) {
            return null;
        }
    }

    private RestTemplate createRestTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) TimeUnit.SECONDS.toMillis(5));
        factory.setReadTimeout((int) TimeUnit.SECONDS.toMillis(10));
        return new RestTemplate(factory);
    }
}
