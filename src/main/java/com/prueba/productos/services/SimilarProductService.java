package com.prueba.productos.services;

import com.prueba.productos.domain.ProductDetail;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class SimilarProductService  {

    @Autowired
    @Qualifier("restTemplateProductsCliente")
    RestTemplate restTemplate;

    public String[] getProductSimilarIds(String productId) throws Exception {
        try{
            ResponseEntity<String[]> respuesta =
                    restTemplate.getForEntity("http://localhost:3001/product/"+productId+"/similarids",
                            String[].class);
            return respuesta.getBody();
        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public ProductDetail getProductById(String productId) throws Exception {
        try{
            ResponseEntity<ProductDetail> respuesta =
                    restTemplate.getForEntity("http://localhost:3001/product/"+productId+"/similarids", ProductDetail.class);
            if (respuesta.getStatusCodeValue() != 404){
                return respuesta.getBody();
            } else {
                return null;
            }

        } catch (Exception e){
            throw new Exception(e.getMessage());
        }
    }

    public List<ProductDetail> getSimilarProductsById(String productId) throws Exception {
        //Comprobamos que el producto existe.
        if (getProductById(productId) == null){
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
        String[] productsSimilarId = getProductSimilarIds(productId);
        List<ProductDetail> productsDetail = new ArrayList<>();
        for (String s: productsSimilarId){
            ProductDetail productDetail = getProductById(s);
            if (productDetail != null)
                productsDetail.add(productDetail);
        }
        return productsDetail.size() > 0 ? productsDetail: null;
    }
}
