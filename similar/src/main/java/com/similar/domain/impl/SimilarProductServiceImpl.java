package com.similar.domain.impl;

import com.similar.domain.SimilarProductService;
import com.similar.infrastructure.client.SimulatedClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Slf4j
@Service
public class SimilarProductServiceImpl implements SimilarProductService {

    SimulatedClient client;

    @Override
    public List<String> getSimilarProductBy(Long productId) {
        this.client.getSimilar(getHeaders(), productId);
        return Collections.emptyList();
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        return headers;
    }
}
