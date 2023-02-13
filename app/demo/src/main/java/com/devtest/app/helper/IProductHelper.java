package com.devtest.app.helper;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;

public interface IProductHelper {
    public String[] getSimilarProductsIds(String id) throws GenericException;
    public Product getProductById(String id) throws GenericException;
}
