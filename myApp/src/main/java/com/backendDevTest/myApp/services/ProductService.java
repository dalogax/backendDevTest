package com.backendDevTest.myApp.services;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.backendDevTest.myApp.exceptions.MyAppGenericException;
import com.backendDevTest.myApp.model.ProductDetail;
import com.backendDevTest.myApp.utils.Utils;

@Service
public class ProductService {

	private static final String HOST_MOCKS = "http://localhost:3001/";
	
	private static final String GET_PRODUCT_SIMILAR_IDS = HOST_MOCKS + "product/{productId}/similarids";
	private static final String GET_PRODUCT_DETAIL = HOST_MOCKS + "product/{productId}";
	
	public List<ProductDetail> getSimilarProductsById(String id) throws MyAppGenericException{
		List<ProductDetail> products = new ArrayList<>();
		
		try {
			RestTemplate restTemplate = new RestTemplate();
			List<Integer> similarids = Utils.castList(Integer.class, restTemplate.getForObject(GET_PRODUCT_SIMILAR_IDS.replace("{productId}", id), ArrayList.class));
			
			products = similarids
					.stream()
					.map(i -> restTemplate.getForObject(GET_PRODUCT_DETAIL.replace("{productId}", i.toString()), ProductDetail.class))
					.collect(Collectors.toList());
			
		} catch (Exception e) {
			throw new MyAppGenericException("An error occurred while obtaining the similar products");
		}
		
		return products;
	}
	
}
