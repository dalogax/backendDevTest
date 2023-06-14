package com.cibernos.prueba.infrastructure.adapter;

import com.cibernos.prueba.application.repository.ProductRepository;
import com.cibernos.prueba.domain.Product;
import org.springframework.stereotype.Repository;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

import static java.lang.Integer.parseInt;
import static java.lang.Math.log;

@Repository
public class ProductRepositoryImpl implements ProductRepository {


    public List<Integer> getProducts(Integer Id) {
        Mono<String[]> responseSpec1 = null;;
        WebClient client = WebClient.create();

        List<Integer> responseSpec2 = client.get()
                .uri("http://localhost:3001/product/" + Id + "/similarids")
                .retrieve()
                .bodyToMono(List.class)
                .cast(List.class)
                .block();
        return responseSpec2;
    }


    public Product getProductsById(Integer Id) {
        WebClient client = WebClient.create();
        Product responseSpec =
                client
                        .get()
                        .uri("http://localhost:3001/product/" + Id)
                        .retrieve().bodyToMono(Product.class).block();
        return responseSpec;
    }
}
