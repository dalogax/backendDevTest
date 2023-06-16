package com.cibernos.prueba.infrastructure.controller;


import com.cibernos.prueba.application.services.ProductService;
import com.cibernos.prueba.domain.Product;
import com.cibernos.prueba.domain.ResponseApi;
import org.springframework.beans.factory.annotation.Autowired;
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
    public ResponseApi getProduct(@PathVariable("id") Integer Id) throws URISyntaxException {
        ResponseApi responseApi = new ResponseApi();

        try {
            Product product = this.productService.getProductById(Id);
            responseApi.setStatus(200);
            responseApi.setMethod("GET");
            responseApi.setPath("/product/" + Id);
            responseApi.setBody(product);
            responseApi.setIsRegexPah(false);
            responseApi.setHeaders("{\"message\":\"Product not found\"}");
            if (product.getId() == 0) {
                responseApi.setStatus(400);
                responseApi.setHeaders("{\"message\":\"Product not found\"}");
            }
        } catch (Exception e) {
            responseApi.setMethod("GET");
            responseApi.setStatus(500);
            responseApi.setPath("/product/" + Id);
            responseApi.setIsRegexPah(false);
            responseApi.setHeaders("{\n" +
                    "    \"Content-Type\": \"application/json\"\n" +
                    "  },");
        }
        return responseApi;
    }

    @GetMapping("/{productId}/similarids")
    public ResponseApi getProductSimilarty(@PathVariable("productId") Integer productId) {
        ResponseApi responseApi = new ResponseApi();
        try {
            responseApi.setMethod("GET");
            responseApi.setPath("/product/" + productId + "/similarids");
            responseApi.setIsRegexPah(false);
            List<Integer> resp = this.productService.getProducts(productId);
            if (resp.isEmpty()) {
                responseApi.setStatus(400);
                responseApi.setBody("\"{\\\"message\\\":\\\"Product not found\\\"}\"");
            }
            responseApi.setStatus(200);
            responseApi.setBody(resp);
        } catch (Exception e) {
            responseApi.setMethod("GET");
            responseApi.setPath("/product/" + productId + "/similarids");
            responseApi.setIsRegexPah(false);
            responseApi.setStatus(500);
            responseApi.setHeaders("{\n" +
                    "    \"Content-Type\": \"application/json\"\n" +
                    "  },");
        }
        return responseApi;
    }
}
