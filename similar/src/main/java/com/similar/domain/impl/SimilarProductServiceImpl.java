package com.similar.domain.impl;

import com.similar.domain.SimilarProductService;

import com.similar.domain.model.Product;
import com.similar.infrastructure.client.SimulatedClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarProductServiceImpl implements SimilarProductService {

    private final SimulatedClient client;

    @Override
    public List<Product> getSimilarProductBy(Long productId) {
        return this.client.getSimilar(getHeaders(), productId)
                .stream()
                .map(this.client::getDetailOfProduct)
                .collect(Collectors.toList());
    }

    // It is not strictly necessary, but to give another example
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
