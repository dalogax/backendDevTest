package com.napptilus.technicalTest.infrastructure.adapters;

import com.napptilus.technicalTest.domain.models.Product;
import com.napptilus.technicalTest.domain.repositories.ProductRepository;
import com.napptilus.technicalTest.infrastructure.data.dto.ProductDto;
import com.napptilus.technicalTest.infrastructure.data.mappers.ProductMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;

import java.util.List;

@Component
public class ProductRepositoryAdapter implements ProductRepository {

    private final WebClient webClient;

    public ProductRepositoryAdapter(@Value("${application.simulado.host}") String applicationSimuladoHost) {
        this.webClient = WebClient.builder().baseUrl(applicationSimuladoHost).build();
    }


    @Override
    public List<Product> findProductsById(List<Integer> ids) {
        Flux<Product> requests = Flux.fromIterable(ids)
                .flatMap(productId -> webClient
                        .get()
                        .uri("/product/{id}", productId)
                        .retrieve()
                        .bodyToMono(ProductDto.class)
                        .map(ProductMapper::toDomain)
                        .onErrorComplete()
                );

        return Flux.concat(requests).collectList().block();
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
