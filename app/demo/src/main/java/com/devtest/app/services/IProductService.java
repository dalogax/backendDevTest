package com.devtest.app.services;

import java.util.List;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;

public interface IProductService {
    /**
    * Service that gets the similar products from a an id
    *
    * @param id  id where we get from the similar products
    * @return List<Product> list of similiar products
    * @throws GenericException  If an input has no valid value or missing 
    *                    
    */
    List<Product> getSimilarProductsById(String productId) throws GenericException;
}
