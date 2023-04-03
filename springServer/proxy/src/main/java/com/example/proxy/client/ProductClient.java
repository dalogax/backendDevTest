package com.example.proxy.client;

import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.dto.SimilarProductsDTO;
import com.example.proxy.utils.Mapper;
import org.springframework.http.HttpStatus;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpConnectTimeoutException;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

public class ProductClient {
    private final String BASE_URL = "http://host.docker.internal:3001/product/";
    private final String SIMILAR_IDS = "/similarids";
    private final long TIME_OUT = 5;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(TIME_OUT)).build();

    public SimilarProductsDTO getSimilarProducts(String productId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s%s%s", BASE_URL, productId, SIMILAR_IDS)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                return Mapper.jsonResponseToSimilarProductsDTO(response.body());
            }

        } catch (Exception e) {

        }
        return null;
    }

    public ProductDetailDTO getProductDetail(String productId) {
        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s%s", BASE_URL, productId)))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value()) {
                return Mapper.jsonResponseToProductDetailDTO(response.body());
            }

            if (response.statusCode() == HttpStatus.NOT_FOUND.value()) {
                throw new Exception();
            }

        } catch (HttpConnectTimeoutException | IOException | InterruptedException ex) {
            throw new Exception(ex);
        }

    }
}
