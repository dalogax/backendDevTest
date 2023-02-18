package com.dev.similarproducts.api;

import com.dev.similarproducts.dto.ProductDetailDto;
import com.dev.similarproducts.exception.ExternalApiException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static com.dev.similarproducts.util.ApplicationConstants.PRODUCT_ID;
import static com.dev.similarproducts.util.ApplicationConstants.SIMILAR_PRODUCTS;


@Service
public class ExternalApiClientImpl implements ExternalApiClient {
    private static final Logger L = LoggerFactory.getLogger(ExternalApiClientImpl.class);
    private final RestTemplate restTemplate;
    @Value("${external.api.base.url}")
    private String externalApiBaseUrl;

    @Autowired
    public ExternalApiClientImpl(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @Override
    public ProductDetailDto getProductDetail(String productId) {
        try {
            String productDetailUrl = externalApiBaseUrl + PRODUCT_ID.replace("{productId}", productId);
            return restTemplate.getForObject(productDetailUrl, ProductDetailDto.class);
        } catch (RestClientException e) {
            L.error("Error while retrieving product detail", e);
            throw new ExternalApiException("Error while retrieving product detail", HttpStatus.NOT_FOUND);
        }
    }

    @Override
    public List<String> getSimilarProductIds(String productId) {
        try {
            String similarProductIdsUrl = externalApiBaseUrl + SIMILAR_PRODUCTS.replace("{productId}", productId);
            return Arrays.asList(Objects.requireNonNull(restTemplate.getForObject(similarProductIdsUrl, String[].class)));
        } catch (RestClientException e) {
            L.error("Error while retrieving similar product ids", e);
            throw new ExternalApiException("Error while retrieving similar product ids", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
