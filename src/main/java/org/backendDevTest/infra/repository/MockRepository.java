package org.backendDevTest.infra.repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Repository;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Repository
public class MockRepository {
    private final Map<String, String> productBodies = new HashMap<>();
    private final Map<String, String> similarIdsBodies = new HashMap<>();

    @PostConstruct
    public void init() {
        ObjectMapper mapper = new ObjectMapper();
        byte[] bytes;
        try (InputStream is = getClass().getResourceAsStream("/mocks.json")) {
            if (is == null) {
                throw new IllegalStateException("Could not find mocks.json in classpath");
            }

            bytes = is.readAllBytes();

            String content = new String(bytes, StandardCharsets.UTF_8);

            JsonNode root = mapper.readTree(content);

            for (JsonNode node : root) {
                String path = node.get("path").asText();
                String body = node.has("body") ? node.get("body").asText() : null;

                if (path.endsWith("/similarids")) {
                    similarIdsBodies.put(path.split("/product/")[1].replace("/similarids", ""), body);
                } else if (path.endsWith("/similarProducts")) {
                    productBodies.put(path.split("/product/")[1].replace("/similarProducts", ""), body);
                } else if (path.matches("/product/\\d+")) {
                    productBodies.put(path.split("/product/")[1], body);
                } else  {
                    productBodies.put(path.split("/product/")[1], body);
                }
            }
        } catch (Exception e) {
            e.getStackTrace();
        }
    }

    public String getAllProducts() throws JsonProcessingException {
        return productBodies.values().toString();
    }

    public String getProduct(String id) {
        return productBodies.get(id);
    }

    public String getSimilarProducts(String id) {
        return productBodies.get(id);
    }

    public String getSimilarIds(String id) {
        return similarIdsBodies.get(id);
    }
}
