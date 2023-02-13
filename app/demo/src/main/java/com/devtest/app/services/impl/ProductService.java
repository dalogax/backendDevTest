package com.devtest.app.services.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.devtest.app.entities.Product;
import com.devtest.app.exceptions.GenericException;
import com.devtest.app.helper.IProductHelper;
import com.devtest.app.helper.impl.ProductHelper;
import com.devtest.app.services.IProductService;
import com.devtest.app.utils.Constants;
import com.devtest.app.utils.Utils;

@Service
public class ProductService implements IProductService {

	@Autowired
    private ProductHelper productsClient;
	
	public List<Product> getSimilarProductsById(String id) throws GenericException{
		if(id == null) throw new GenericException("Product " + id + " not found");

		List<Product> products = new ArrayList<>();
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			//TODO: refacto castLIst
			List<Integer> similarids = Utils.castList(Integer.class, restTemplate.getForObject(Constants.GET_PRODUCT_SIMILAR_IDS.replace("{productId}", id), ArrayList.class));
			
			products = similarids
					.stream()
					.map(i -> restTemplate.getForObject(Constants.GET_PRODUCT_DETAIL.replace("{productId}", i.toString()), Product.class))
					.collect(Collectors.toList());
			
			String[] similarProductsIds =  productsClient.getSimilarProductsIds(productId);
			if( similarProductsIds == null) return null;

			for (String id : similarProductsIds) {
			Product product = productsClient.getProductById(id);
			if( product != null) {
				products.add(product);
			}
			}	
		} 
		//TODO: manage better exceptions
		catch (Exception e) {
			throw new GenericException("An error occurred while obtaining the similar products");
		}
		
		return products;
	}
	
}