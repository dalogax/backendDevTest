package com.devtest.app.controllers;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;

public interface IProductController {
    public ResponseEntity<List<Product>> getSimilarProductsById(@PathVariable("productId") String id) throws GenericException;
}
