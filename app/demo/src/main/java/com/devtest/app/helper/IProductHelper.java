package com.devtest.app.helper;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;

public interface IProductHelper {
    /**
    * Helper that gets the similar products from a an id
    *
    * @param id  id where we get from the similar products
    * @return String[] list of id similiar products
    * @throws GenericException  If an input has no valid value or missing 
    *                    
    */
    public String[] getSimilarProductsIds(String id) throws GenericException;

    /**
    * Helper that gets a product from an id
    *
    * @param id  id where we get product
    * @return Product
    * @throws GenericException  If an input has no valid value or missing 
    *                    
    */
    public Product getProductById(String id) throws GenericException;
}
