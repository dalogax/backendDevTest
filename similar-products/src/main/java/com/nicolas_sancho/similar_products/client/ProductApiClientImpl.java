package com.nicolas_sancho.similar_products.client;

import com.nicolas_sancho.similar_products.dto.ProductDetailDTO;
import org.springframework.beans.factory.annotation.Value;
import com.nicolas_sancho.similar_products.exception.NotFoundException;
import io.github.resilience4j.retry.annotation.Retry;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductApiClientImpl implements ProductApiClient {

    private final RestTemplate restTemplate;

    @Value("${external.api.base-url}")
    private String baseUrl;

    @Cacheable(value = "similar-ids-cache", key = "#productId")
    @Override
    @Retry(name = "similarIds", fallbackMethod = "getSimilarIdsFallback")
    public List<String> getSimilarIds(String productId) {
        try {
            String[] ids = restTemplate.getForObject(
                    baseUrl + "/product/" + productId + "/similarids",
                    String[].class);
            return Arrays.asList(ids);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Product " + productId + " not found");
        }
    }

    public List<String> getSimilarIdsFallback(String productId, Exception ex) {
        throw new NotFoundException("Failed to retrieve similar products for " + productId + " after retries");
    }

    @Cacheable(value = "product-details-cache", key = "#productId")
    @Override
    @Retry(name = "productDetail", fallbackMethod = "getProductDetailFallback")
    public ProductDetailDTO getProductDetail(String productId) {
        try {
            return restTemplate.getForObject(
                    baseUrl + "/product/" + productId,
                    ProductDetailDTO.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Product " + productId + " not found");
        }
    }

    public ProductDetailDTO getProductDetailFallback(String productId, Exception ex) {
        throw new NotFoundException("Failed to retrieve product details for " + productId + " after retries");
    }
}
