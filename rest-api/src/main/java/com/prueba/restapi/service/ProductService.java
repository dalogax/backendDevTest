package com.prueba.restapi.service;

import java.util.Arrays;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.ResourceAccessException;
import org.springframework.web.client.RestTemplate;

import com.prueba.restapi.dto.ProductDto;
import com.prueba.restapi.exception.ConnectionException;
import com.prueba.restapi.exception.GenericException;
import com.prueba.restapi.exception.NotFoundException;

@Service
public class ProductService {

    @Autowired
    private RestTemplate restTemplate;

    @Value("${mocks.url}")
    private String url;

    @PostConstruct
    protected void init() {
        url = url + "/product/";
    }

    public List<Integer> getProductSimilarIds(int id) {
        try {
            return Arrays.asList(restTemplate.getForObject(url + id + "/similarids", Integer[].class));
        } catch (ResourceAccessException e) {
            throw new ConnectionException("Can't connect to " + url);
        } catch (HttpClientErrorException e) {
            throw new NotFoundException("Not found code returned from url");
        } catch (Exception e) {
            throw new GenericException("Something goes wrong");
        }
    }

    public ProductDto getProductById(int id) {
        try {
            return restTemplate.getForObject(url + id, ProductDto.class);
        } catch (ResourceAccessException e) {
            throw new ConnectionException("Can't connect to " + url);
        } catch (HttpClientErrorException e) {
            throw new NotFoundException("Not found code returned from url");
        } catch (Exception e) {
            throw new GenericException("Something goes wrong");
        }
    }
}
