package com.zara.client;


import com.zara.dto.ProductDetailDTO;

import com.zara.exception.handler.ProductErrorHandler;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.util.List;

@Component
@Slf4j
public class ProductClient {

    private final WebClient webClient;
    private String similarIdUrl;
    private ProductErrorHandler productErrorHandler;

    public ProductClient(WebClient webClient){
        this.webClient = webClient;
    }

    public Mono<ProductDetailDTO> getProduct(String id){
        var url = similarIdUrl + "/{id}";
        log.info("calling product detail endpoint with id: {}", id);
        return webClient
                .get()
                .uri(url, id)
                .retrieve()
                .bodyToMono(ProductDetailDTO.class);
    }

    public Flux<String> retrieveSimilarIds(String id){
        var url = similarIdUrl + "/{id}/similarids";
        log.info("calling product similar ids endpoint with id: {}", id);
        return webClient
                .get()
                .uri(url, id)
                .retrieve()
                .onStatus(HttpStatus::isError,productErrorHandler::handleError)
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .flatMapMany(Flux::fromIterable)
                .log();
    }

    @Value("${rest-client.product.url}")
    public void setSimilarIdUrl(String similarIdUrl) {
        this.similarIdUrl = similarIdUrl;
    }

    @Autowired
    public void setProductErrorHandler(ProductErrorHandler productErrorHandler) {
        this.productErrorHandler = productErrorHandler;
    }
}
