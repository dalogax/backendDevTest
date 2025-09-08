package org.backendDevTest.application.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.backendDevTest.infra.model.ProductDetail;
import org.backendDevTest.infra.repository.MockRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class ProductService {

    private final MockRepository mockRepository;
    private final ObjectMapper mapper = new ObjectMapper();

    public ProductService(MockRepository mockRepository) {
        this.mockRepository = mockRepository;
    }

    public List<ProductDetail> getProducts() throws JsonProcessingException {
        String body = mockRepository.getAllProducts();
        return getProductDetails(body);
    }

    public ProductDetail getProductId(String productId) throws JsonProcessingException {
        return Optional.ofNullable(mapper.readValue(mockRepository.getProduct(productId), ProductDetail.class))
                .orElse(new ProductDetail());
    }

    public List<ProductDetail> getSimilarProduct(String productId) throws JsonProcessingException {
        String body = mockRepository.getSimilarProducts(productId);
        return getProductDetails(body);
    }

    public List<String> getSimilarIds(String productId) throws JsonProcessingException {
        String body = mockRepository.getSimilarIds(productId);

        JsonNode node = mapper.readTree(body);
        if (node.isArray()) {
            if (!node.isEmpty() && node.get(0).has("message")) {
                return List.of();
            }
            return mapper.readValue(body, new TypeReference<List<String>>() {});
        }

        return List.of();
    }

    private List<ProductDetail> getProductDetails(String body) throws JsonProcessingException {
        if (body == null || body.isBlank()) {
            return List.of();
        }

        List<JsonNode> items = mapper.readValue(body, new TypeReference<>() {});
        List<ProductDetail> products = new ArrayList<>();

        for (JsonNode item : items) {
            if (item != null && item.isObject() && item.has("id")) {
                products.add(mapper.treeToValue(item, ProductDetail.class));
            }
        }

        return products;
    }
}
