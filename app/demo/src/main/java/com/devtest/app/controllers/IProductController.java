package com.devtest.app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;

public interface IProductController {
    /**
    * Controller that gets the similar products from an id
    *
    * @param id  id where we get from the similar products
    * @return List<Product> list of similiar products
    * @throws GenericException  If an input has no valid value or missing 
    *                    
    */
    public ResponseEntity<List<Product>> getSimilarProductsById(@PathVariable("productId") String id) throws GenericException;
}
