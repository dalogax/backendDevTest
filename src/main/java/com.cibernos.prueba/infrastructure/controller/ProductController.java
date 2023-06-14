package com.cibernos.prueba.infrastructure.controller;

import ch.qos.logback.core.net.server.Client;
import com.cibernos.prueba.application.services.ProductService;
import com.cibernos.prueba.domain.Product;
import com.cibernos.prueba.domain.ResponseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;
import java.nio.charset.StandardCharsets;
import java.time.Duration;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpClient.Redirect;
import java.net.http.HttpClient.Version;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpResponse.BodyHandlers;

import java.io.IOException;
import java.net.URI;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("product")
public class ProductController {
    @Autowired
    private ProductService productService;


    @GetMapping("/{id}")
    public Product getProduct(@PathVariable("id") Integer Id) throws URISyntaxException {
         return this.productService.getProductById(Id);
    }

    @GetMapping("/{productId}/similarids")
    public List<Integer> getProductSimilarty(@PathVariable("productId") Integer productId) {
        return this.productService.getProducts(productId);
    }
}
