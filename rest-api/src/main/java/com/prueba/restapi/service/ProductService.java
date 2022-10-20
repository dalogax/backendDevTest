package com.prueba.restapi.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.prueba.restapi.dto.ProductDto;

@Service
public class ProductService {

    @Value("${mocks.url}")
    private String url;

    @PostConstruct
    protected void init() {
        url = url + "/product/";
    }

    public List<Integer> getProductSimilarIds(int id) {
        RestTemplate restTemplate = new RestTemplate();
        return Arrays.asList(restTemplate.getForObject(url + id + "/similarids", Integer[].class));
    }

    public ProductDto getProductById(int id) {
        RestTemplate restTemplate = new RestTemplate();
        return restTemplate.getForObject(url + id, ProductDto.class);
    }
}
