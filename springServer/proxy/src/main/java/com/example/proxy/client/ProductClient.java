package com.example.proxy.client;

import com.example.proxy.controller.ProductController;
import com.example.proxy.dto.ProductDetailDTO;
import com.example.proxy.dto.SimilarProductsDTO;
import com.example.proxy.exceptions.APIException;
import com.example.proxy.exceptions.NotFoundException;
import com.example.proxy.utils.Mapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

@Component
public class ProductClient {
    private static final Logger logg = LogManager.getLogger(ProductController.class);
    private final String BASE_URL = "http://host.docker.internal:3001/product/";
    private final String SIMILAR_IDS = "/similarids";
    private final long TIME_OUT = 5;
    private final HttpClient httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(TIME_OUT)).build();

    public SimilarProductsDTO getSimilarProducts(String productId) {
        HttpResponse<String> response;
        SimilarProductsDTO similarProductsDTO = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s%s%s", BASE_URL, productId, SIMILAR_IDS)))
                    .GET()
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value())
                similarProductsDTO = Mapper.jsonResponseToSimilarProductsDTO(response.body());

        } catch (Exception exception) {
            logg.info(String.format("getSimilarProducts - Catch : %s", exception));
            throw new APIException(exception.getMessage(), exception);
        }

        if (response.statusCode() == HttpStatus.NOT_FOUND.value())
            throw new NotFoundException("Product not Found");

        return similarProductsDTO;
    }

    public ProductDetailDTO getProductDetail(String productId) {
        HttpResponse<String> response;
        ProductDetailDTO productDetailDTO = null;

        try {
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(String.format("%s%s", BASE_URL, productId)))
                    .GET()
                    .build();

            response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == HttpStatus.OK.value())
                productDetailDTO = Mapper.jsonResponseToProductDetailDTO(response.body());

        } catch (Exception exception) {
            logg.info(String.format("getProductDetail - Catch : %s", exception));
            throw new APIException(exception.getMessage(), exception);
        }

        if (response.statusCode() == HttpStatus.NOT_FOUND.value())
            throw new NotFoundException("Product not Found");

        return productDetailDTO;

    }
}
