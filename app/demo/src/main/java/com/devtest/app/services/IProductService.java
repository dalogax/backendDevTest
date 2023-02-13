package com.devtest.app.services;

import java.util.List;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;

public interface IProductService {
    List<Product> getSimilarProductsById(String productId) throws GenericException;
}
