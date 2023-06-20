package com.pablolizarraga.solution.infrastructure.adapter.out.repository.mock;

import com.pablolizarraga.solution.application.port.out.SimilarProductRepositoryPort;
import com.pablolizarraga.solution.domain.ProductDetail;
import jakarta.annotation.PostConstruct;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;
import java.util.Optional;

@NoArgsConstructor
@Repository
public class SimilarProductRepositoryMock implements SimilarProductRepositoryPort {

    private static final String GET_SIMILAR_PRODUCTS = "/product/{productId}/similarids";
    private static final String GET_PRODUCT = "/product/{productId}";

    @Value("${conf.base.url.mocks}")
    private String mocksURL;

    private WebClient webClient;

    public SimilarProductRepositoryMock(WebClient webClient) {
        this.webClient = webClient;
    }

    @PostConstruct
    private void setUpWebClient() {
        webClient = WebClient.create(mocksURL);
    }

    @Override
    public List<String> getSimilarProductIds(String productId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_SIMILAR_PRODUCTS)
                        .build(productId))
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();
    }

    @Override
    public Optional<ProductDetail> getProduct(String productId) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path(GET_PRODUCT)
                        .build(productId))
                .retrieve()
                .bodyToMono(ProductDetail.class)
                .blockOptional();
    }
}
