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
import java.net.URISyntaxException;
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
