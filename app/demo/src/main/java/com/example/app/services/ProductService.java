package com.example.app.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.example.app.exceptions.GenericException;
import com.example.app.entities.Product;
import com.example.app.utils.Constants;
import com.example.app.utils.Utils;

@Service
public class ProductService {
	public List<Product> getSimilarProductsById(String id) throws GenericException{
		List<Product> products = new ArrayList<>();
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			//TODO: refacto castLIst
			List<Integer> similarids = Utils.castList(Integer.class, restTemplate.getForObject(Constants.GET_PRODUCT_SIMILAR_IDS.replace("{productId}", id), ArrayList.class));
			
			products = similarids
					.stream()
					.map(i -> restTemplate.getForObject(Constants.GET_PRODUCT_DETAIL.replace("{productId}", i.toString()), Product.class))
					.collect(Collectors.toList());
			
		} 
		//TODO: manage better exceptions
		catch (Exception e) {
			throw new GenericException("An error occurred while obtaining the similar products");
		}
		
		return products;
	}
	
}