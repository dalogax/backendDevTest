package com.napptilus.technicalTest.infrastructure.adapters;

import com.napptilus.technicalTest.domain.models.Product;
import com.napptilus.technicalTest.domain.repositories.ProductRepository;
import com.napptilus.technicalTest.infrastructure.data.dto.ProductDto;
import com.napptilus.technicalTest.infrastructure.data.mappers.ProductMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final WebClient webClient;

    public ProductRepositoryAdapter() {
        this.webClient = WebClient.builder().baseUrl("http://localhost:3001").build();
    }


    @Override
    public List<Product> findProductsById(List<Integer> ids) {
        Flux<ProductDto> requests = Flux.fromIterable(ids)
                .flatMap(productId ->
                        webClient
                                .get()
                                .uri("/product/{id}", productId)
                                .retrieve()
                                .bodyToMono(ProductDto.class)
                );

        return Flux.concat(requests).collectList().block().stream().map(ProductMapper::toDomain).toList();
    }

    @Override
    public List<Integer> findRelatedIds(Integer id) {
        Flux<Integer> relatedIdsFlux = webClient
                .get()
                .uri("product/{productId}/similarids", id)
                .retrieve()
                .bodyToFlux(Integer.class);


        return relatedIdsFlux.collectList().block();

    }
}
