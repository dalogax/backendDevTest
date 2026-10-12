package com.backendtest.similar.infrastructure.rest;

import com.backendtest.similar.dto.ProductDetail;
import com.backendtest.similar.exception.ProductNotFoundException;
import com.backendtest.similar.exception.UpstreamException;
import com.backendtest.similar.port.ProductCatalogPort;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RestProductClient implements ProductCatalogPort {

    private final RestClient restClient;
    private final ObjectMapper objectMapper;

    public RestProductClient(RestClient productRestClient, ObjectMapper objectMapper) {
        this.restClient = productRestClient;
        this.objectMapper = objectMapper;
    }

    @Override
    public List<String> getSimilarIds(String productId) {
        final String body;
        try {
            body = restClient
                    .get()
                    .uri("/product/{id}/similarids", productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(s -> s.value() == 404, (req, res) -> {
                        throw new ProductNotFoundException(productId);
                    })
                    .onStatus(s -> s.is5xxServerError(), (req, res) -> {
                        throw new UpstreamException("similarids 5xx for " + productId);
                    })
                    .body(String.class);
        } catch (ProductNotFoundException | UpstreamException e) {
            throw e;
        } catch (org.springframework.web.client.HttpClientErrorException.NotFound e) {
            throw new ProductNotFoundException(productId);
        } catch (Exception e) {
            throw new UpstreamException("similarids failed for " + productId, e);
        }
        return normalizeIds(body);
    }

    @Override
    @Cacheable(value = "details", key = "#productId", unless = "#result == null")
    public Optional<ProductDetail> getProduct(String productId) {
        try {
            ProductDetail detail = restClient
                    .get()
                    .uri("/product/{id}", productId)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(s -> s.value() == 404, (req, res) -> {
                        throw new ProductNotFoundException(productId);
                    })
                    .onStatus(s -> s.is5xxServerError(), (req, res) -> {
                        throw new UpstreamException("product 5xx for " + productId);
                    })
                    .body(ProductDetail.class);
            if (!isValid(detail)) {
                return Optional.empty();
            }
            return Optional.of(detail);
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    List<String> normalizeIds(String body) {
        if (body == null || body.isBlank()) {
            return List.of();
        }
        final JsonNode root;
        try {
            root = objectMapper.readTree(body);
        } catch (Exception e) {
            return List.of();
        }
        if (!root.isArray()) {
            return List.of();
        }
        List<String> ids = new ArrayList<>();
        for (JsonNode node : root) {
            if (node.isNumber()) {
                ids.add(node.asText());
            } else if (node.isTextual()) {
                String t = node.asText().trim();
                if (!t.isEmpty()) {
                    ids.add(t);
                }
            }
        }
        return ids;
    }

    private boolean isValid(ProductDetail d) {
        return d != null
                && d.id() != null
                && !d.id().isBlank()
                && d.name() != null
                && !d.name().isBlank()
                && d.price() != null
                && d.availability() != null;
    }
}
