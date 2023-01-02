package com.prueba.productos.web;


import com.prueba.productos.domain.ProductDetail;
import com.prueba.productos.services.SimilarProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;



@RestController
@RequestMapping(value = "/product")
@CrossOrigin(origins = "*", methods= {RequestMethod.GET,RequestMethod.POST,RequestMethod.PUT,RequestMethod.DELETE})
public class SimilarProductController {
    
    @Autowired
    SimilarProductService similarProductService;

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    @GetMapping(value = "/{productId}/similar", produces = MediaType.APPLICATION_JSON_VALUE)
    public @ResponseBody
    List<ProductDetail> getProductSimilar(
            HttpServletRequest request,
            @PathVariable String productId) throws Exception {
        List<ProductDetail> productosSimilares = similarProductService.getSimilarProductsById(productId);
        if (productosSimilares != null){
            return productosSimilares;
        } else{
            throw new HttpClientErrorException(HttpStatus.NOT_FOUND);
        }
    }


}
