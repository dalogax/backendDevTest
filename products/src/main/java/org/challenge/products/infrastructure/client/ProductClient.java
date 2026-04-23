package org.challenge.products.infrastructure.client;

import org.challenge.products.application.exception.ExternalServiceException;
import org.challenge.products.application.port.out.ProductPort;
import org.challenge.products.domain.exception.ProductNotFoundException;
import org.challenge.products.domain.model.Product;
import org.challenge.products.infrastructure.dto.ProductDetailDto;
import org.challenge.products.infrastructure.mapper.ProductMapper;
import org.challenge.products.util.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Component
public class ProductClient implements ProductPort {

    private static final Logger log = LoggerFactory.getLogger(ProductClient.class);

    private final RestClient productRestClient;
    private final ProductMapper productMapper;

    public ProductClient(RestClient productRestClient, ProductMapper productMapper) {
        this.productRestClient = productRestClient;
        this.productMapper = productMapper;
    }

    @Override
    @Cacheable(value = "similarIds", key = "#productId")
    public List<String> getSimilarProductIds(String productId) {
        log.debug("Fetching similar product ids for productId: {}", productId);
        String[] ids = productRestClient.get()
                .uri("/{productId}/similarids", productId)
                .retrieve()
                .onStatus(HttpStatus.NOT_FOUND::equals,
                        (req, res) -> {
                            log.error("Similar products for id: {} not found", productId);
                            throw new ProductNotFoundException("Similar products for id: {} not found - body response: {}", productId, StringUtil.readString(res.getBody()));
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            log.error("Error fetching similar ids for id: {} - body response: {}", productId, StringUtil.readString(res.getBody()));
                            throw new ExternalServiceException("External service error fetching similar ids for: " + productId,
                                    StringUtil.readString(res.getBody()));
                        })
                .body(String[].class);

        List<String> result = Optional.ofNullable(ids)
                .map(Arrays::asList)
                .orElse(List.of());

        log.debug("Found {} similar product ids for productId: {}", result.size(), productId);
        return result;
    }

    @Override
    @Cacheable(value = "productDetail", key = "#productId")
    public Product getProduct(String productId) {
        log.debug("Fetching product details for productId: {}", productId);

        ProductDetailDto responseBody = productRestClient.get()
                .uri("/{productId}", productId)
                .retrieve()
                .onStatus(status -> status.value() == 404,
                        (req, res) -> {
                            log.error("Product with id: {} not found", productId);
                            throw new ProductNotFoundException(productId, StringUtil.readString(res.getBody()));
                        })
                .onStatus(HttpStatusCode::is5xxServerError,
                        (req, res) -> {
                            log.error("Error fetching product details for id: {} - body response: {}",
                                    productId, StringUtil.readString(res.getBody()));
                            throw new ExternalServiceException("GET /" + productId + " details",
                                    StringUtil.readString(res.getBody()));
                        })
                .body(ProductDetailDto.class);

        Product result = productMapper.toModel(responseBody);
        log.debug("Successfully fetched product details for productId: {}", productId);
        return result;
    }
}