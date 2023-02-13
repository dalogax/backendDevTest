package com.devtest.app.services.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;
import com.devtest.app.helper.impl.ProductHelper;
import com.devtest.app.services.IProductService;

@Service
public class ProductService implements IProductService {

	@Autowired
    private ProductHelper productHelper;
	
	public List<Product> getSimilarProductsById(String id) throws GenericException{
		if(id == null) throw new GenericException("Product " + id + " not found");

		List<Product> products = new ArrayList<>();
		
		String[] similarProductsIds = productHelper.getSimilarProductsIds(id);
		if(similarProductsIds == null) throw new GenericException("Not similar products found");

		for (String similarId : similarProductsIds) {
			Product product = productHelper.getProductById(similarId);
			if (product != null) {
				products.add(product);
			}
		}	
		
		return products;
	}
}