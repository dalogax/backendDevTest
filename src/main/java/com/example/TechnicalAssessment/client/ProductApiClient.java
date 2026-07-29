package com.example.TechnicalAssessment.client;

import com.example.TechnicalAssessment.exception.ExternalApiException;
import com.example.TechnicalAssessment.exception.ProductNotFoundException;
import com.example.TechnicalAssessment.model.ProductDetail;
import java.util.Arrays;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatusCode;
import org.springframework.stereotype.Component;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestClient;

@Component
public class ProductApiClient {

    private final RestClient restClient;

    public ProductApiClient(
            RestClient.Builder restClientBuilder,
            @Value("${external.api.base-url:http://localhost:3001}") String externalApiBaseUrl
    ) {
        this.restClient = restClientBuilder
                .baseUrl(externalApiBaseUrl)
                .build();
    }

    public List<String> getSimilarProductIds(String productId) {
        try {
            String[] similarIds = restClient.get()
                    .uri("/product/{productId}/similarids", productId)
                    .retrieve()
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ExternalApiException(
                                "Error retrieving similar product ids for product " + productId
                        );
                    })
                    .body(String[].class);

            return similarIds == null ? List.of() : Arrays.asList(similarIds);
        } catch (ResourceAccessException exception) {
            throw new ExternalApiException(
                    "External API is unavailable while retrieving similar product ids for product "
                            + productId,
                    exception
            );
        }
    }

    public ProductDetail getProductDetail(String productId) {
        try {
            return restClient.get()
                    .uri("/product/{productId}", productId)
                    .retrieve()
                    .onStatus(
                            statusCode -> statusCode.value() == 404,
                            (request, response) -> {
                                throw new ProductNotFoundException("Product not found: " + productId);
                            }
                    )
                    .onStatus(HttpStatusCode::isError, (request, response) -> {
                        throw new ExternalApiException("Error retrieving product detail for product " + productId);
                    })
                    .body(ProductDetail.class);
        } catch (ResourceAccessException exception) {
            throw new ExternalApiException(
                    "External API is unavailable while retrieving product detail for product " + productId,
                    exception
            );
        }
    }
}
