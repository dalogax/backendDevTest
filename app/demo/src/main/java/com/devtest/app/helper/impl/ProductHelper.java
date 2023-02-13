package com.devtest.app.helper.impl;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;
import com.devtest.app.helper.IProductHelper;
import com.devtest.app.utils.Constants;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Repository;
import org.springframework.web.client.RestTemplate;

@Repository
public class ProductHelper implements IProductHelper{
    public String[] getSimilarProductsIds(String id) throws GenericException {
        RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String[]> response =
                    restTemplate.getForEntity(Constants.HOST_MOCKS + Constants.PRODUCT + "/" + id + Constants.PRODUCT_SIMILARS_ID,
                            String[].class);
            return response.getBody();
            //DOC: No hay try catch porque un error al mock no es esperado
    }

    public Product getProductById(String id) throws GenericException {
        RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<Product> response = restTemplate.getForEntity(Constants.HOST_MOCKS + Constants.PRODUCT + "/" + id, Product.class);
            return response.getBody();
            //DOC: No hay try catch porque un error al mock no es esperado
    }
}
