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
import java.util.Objects;
import java.util.stream.Collectors;

import static java.lang.String.format;

@Slf4j
@Service
@RequiredArgsConstructor
public class SimilarProductServiceImpl implements SimilarProductService {

    public static final String MSG_ERROR_WHEN_CLIENT_THROW_EXCEPTION = "Client exception when get detail of product (%s)";
    private final SimulatedClient client;

    @Override
    public List<Product> getSimilarProductBy(Long productId) {
        return this.client.getSimilar(getHeaders(), productId)
                .stream()
                .map(this::getDetailOfProduct)
                .filter(Objects::nonNull)
                .filter(Product::availability)
                .collect(Collectors.toList());
    }

    private Product getDetailOfProduct(Long productId) {
        try {
           return this.client.getDetailOfProduct(productId);
        } catch (Exception e){
            log.error(format(MSG_ERROR_WHEN_CLIENT_THROW_EXCEPTION, productId));
        }
        return null;
    }

    // It is not strictly necessary, but to give another example
    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
