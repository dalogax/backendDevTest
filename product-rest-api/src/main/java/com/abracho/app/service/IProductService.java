package com.abracho.app.service;

import com.abracho.app.model.ProductDto;

import reactor.core.publisher.Mono;

public interface IProductService {

    Mono<ProductDto> getProductById(String id);

    Mono<String> getSimilarids(String id);

}
