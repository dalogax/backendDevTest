package com.cibernos.prueba.infrastructure.controller;

import com.cibernos.prueba.application.services.ProductService;
import com.cibernos.prueba.domain.Product;
import com.cibernos.prueba.domain.ResponseApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("product")
public class ProductController {
    @Autowired
    private ProductService productService;


    @GetMapping("/{id}")
    public ResponseApi getProduct(@PathVariable("id") Integer Id) {
        Product product = this.productService.getProductById(Id);
        ResponseApi responseApi = new ResponseApi();
        responseApi.setBody(product);
        responseApi.setMethod("GET");
        responseApi.setStatus(200);
        responseApi.setPath("/product/" + Id);
        responseApi.setIsRegexPah(false);
        return responseApi;
    }

    @GetMapping("/{productId}/similarids")
    public ResponseApi getProductSimilarty(@PathVariable("productId") Integer productId) {
        Iterable<Product> iterable = this.productService.getProducts(productId);
        ResponseApi responseApi = new ResponseApi();
        responseApi.setBody(iterable);
        responseApi.setMethod("GET");
        responseApi.setStatus(200);
        responseApi.setPath("/product/"+productId+"/similarids");
        responseApi.setIsRegexPah(false);
        return responseApi;
    }
}
